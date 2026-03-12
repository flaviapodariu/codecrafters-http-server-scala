package codecrafters_http_server

import codecrafters_http_server.models.RequestHeader.USER_AGENT
import codecrafters_http_server.models.{HttpRequest, HttpResponse, StatusCode, UserAgent, Home, Echo, Files}

import java.io.{BufferedReader, IOException, InputStreamReader}
import java.net.{ServerSocket, Socket}
import java.util.concurrent.Executors
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

val CRLF = "\r\n"
val HTTP_VERSION = "HTTP/1.1"

def parseArgs(args: List[String]): Map[String, String] = {
  args match {
    case "--directory" :: value :: tail =>
      parseArgs(tail) + ("directory" -> value.strip())
    case _ :: tail =>
      parseArgs(tail)
    case Nil => Map.empty
  }
}

@main def main(args: String*): Unit = {
  val config = parseArgs(args.toList)
  val serverSocket = new ServerSocket(4221)
  serverSocket.setReuseAddress(true)

  val threadPoolSize = 32
  given ec: ExecutionContext = ExecutionContext.fromExecutor(
    Executors.newFixedThreadPool(threadPoolSize)
  )
  
  while (true) {
    val clientSocket = serverSocket.accept()
    println(
      s"accepted new connection from ${clientSocket.getChannel()}:${clientSocket.getPort()}"
    )

    Future {
      handleClient(config, HTTP_VERSION, clientSocket)
    }.onComplete {
      case Success(_) => println("OK")
      case Failure(e) => println(s"Error handling client: ${e.getMessage}")
    }
  }

}

def handleClient(config: Map[String, String], httpVersion: String, clientSocket: Socket): Unit = {
  try {
    val inputStream = clientSocket.getInputStream()
    val reader = new BufferedReader(new InputStreamReader(inputStream))

    val rawRequest = Iterator
      .continually(reader.readLine())
      .takeWhile(it => it != null && it.nonEmpty)
      .toList

    val request = HttpRequest.parse(rawRequest)

    val outputStream = clientSocket.getOutputStream()

    val response = request.line.path match {
      case "/" => Home().execute(request)
      case echo if echo.startsWith("/echo/") => Echo(echo).execute(request)
      case userAgent if userAgent.startsWith("/user-agent") => UserAgent().execute(request)
      case files if files.startsWith("/files/") => Files(config.getOrElse("directory", "."), files).execute(request)
      case _ => HttpResponse(StatusCode.NOT_FOUND)
    }
    val responseBytes = response.toBytes(httpVersion)
    outputStream.write(responseBytes)
    outputStream.flush()
    val a = String(responseBytes).replace("\r\n", "\\r\\n")
    println(s"Response $a was sent to client on port ${clientSocket.getPort}")
  } catch {
    case e: IOException =>
      println(s"IOException: ${e.getMessage}")
  } finally {
      clientSocket.close()
  }
}
