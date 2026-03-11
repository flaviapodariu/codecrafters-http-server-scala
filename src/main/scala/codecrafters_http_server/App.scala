package codecrafters_http_server

import java.io.IOException
import java.net.{ServerSocket, Socket}
import java.io.InputStream
import java.io.BufferedReader
import java.io.InputStreamReader
import codecrafters_http_server.models.StatusCode
import codecrafters_http_server.models.HttpResponse
import codecrafters_http_server.models.HttpRequest
import codecrafters_http_server.models.HttpVerb
import codecrafters_http_server.models.HttpVersion
import codecrafters_http_server.models.RequestLine
import codecrafters_http_server.models.RequestHeader.USER_AGENT

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

@main def main(): Unit = {
  val CRLF = "\r\n"
  val HTTP_VERSION = "HTTP/1.1"
  val serverSocket = new ServerSocket(4221)
  serverSocket.setReuseAddress(true)

  while (true) {
    val clientSocket = serverSocket.accept()
    println(
      s"accepted new connection from ${clientSocket.getChannel()}:${clientSocket.getPort()}"
    )

    Future {
      handleClient(HTTP_VERSION, clientSocket)
    }.onComplete {
      case Success(_) => println("OK")
      case Failure(e) => println(s"Error handling client: ${e.getMessage}")
    }
  }

}

def handleClient(httpVersion: String, clientSocket: Socket): Unit = {
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
      case "/"                               => HttpResponse(StatusCode.OK)
      case echo if echo.startsWith("/echo/") =>
        HttpResponse(StatusCode.OK, body = echo.stripPrefix("/echo/"))
      case userAgent if userAgent.startsWith("/user-agent") =>
        HttpResponse(
          StatusCode.OK,
          body = request.headers.getOrElse(USER_AGENT, "")
        )
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
