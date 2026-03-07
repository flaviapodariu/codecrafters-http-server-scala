package codecrafters_http_server

import java.io.IOException
import java.net.ServerSocket
import java.io.InputStream
import java.io.BufferedReader
import java.io.InputStreamReader

@main def main(): Unit = {
  val CRLF = "\r\n"

  try {
    val serverSocket = new ServerSocket(4221)

    serverSocket.setReuseAddress(true)

    val clientSocket = serverSocket.accept()

    println(
      s"accepted new connection from ${clientSocket.getChannel()}:${clientSocket.getPort()}"
    )
    val inputStream = clientSocket.getInputStream()
    val reader = new BufferedReader(new InputStreamReader(inputStream))

    val request = Iterator.continually(reader.readLine())
      .takeWhile(it => it != null && it.nonEmpty)
      .toList

    val Array(verb, path, httpVersion) = request.head.split(" ")

    val headers = request.drop(1)
    println(s"Headers: $headers")

    val outputStream = clientSocket.getOutputStream()

    val supportedPath = "/"
    val status = if (path == supportedPath) "200 OK" else "404 Not Found"

    outputStream.write(s"$httpVersion $status$CRLF$CRLF".getBytes())
  } catch {
    case e: IOException =>
      println(s"IOException: ${e.getMessage}")
  }
}