package codecrafters_http_server

import java.io.IOException
import java.net.ServerSocket
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

@main def main(): Unit = {
  val CRLF = "\r\n"
  val HTTP_VERSION = "HTTP/1.1"

  try {
    val serverSocket = new ServerSocket(4221)

    serverSocket.setReuseAddress(true)

    val clientSocket = serverSocket.accept()

    println(
      s"accepted new connection from ${clientSocket.getChannel()}:${clientSocket.getPort()}"
    )
    val inputStream = clientSocket.getInputStream()
    val reader = new BufferedReader(new InputStreamReader(inputStream))

    val rawRequest = Iterator
      .continually(reader.readLine())
      .takeWhile(it => it != null && it.nonEmpty)
      .toList

    val request = HttpRequest.parse(rawRequest)

    println(s"Headers: ${request.headers}")

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

    outputStream.write(response.toBytes(HTTP_VERSION))

  } catch {
    case e: IOException =>
      println(s"IOException: ${e.getMessage}")
  }
}
