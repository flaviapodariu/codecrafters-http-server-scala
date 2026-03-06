package codecrafters_http_server

import java.io.IOException
import java.net.ServerSocket
import java.io.InputStream
import java.io.BufferedReader
import java.io.InputStreamReader

object Main extends App {
  // You can use print statements as follows for debugging, they'll be visible when running tests.
  System.err.println("Logs from your program will appear here!")

  try {
    val serverSocket = new ServerSocket(4221)

    serverSocket.setReuseAddress(true)

    val clientSocket = serverSocket.accept()

    println(
      s"accepted new connection from ${clientSocket.getChannel()}:${clientSocket.getPort()}"
    )
    val inputStream = clientSocket.getInputStream()
    val reader = new BufferedReader(new InputStreamReader(inputStream))

    val request = reader.readLine()

    println(s"request: $request")

    val outputStream = clientSocket.getOutputStream()

    val httpVersion = "HTTP/1.1"
    val status = "200 OK"
    val CRLF = "\r\n"
    outputStream.write(s"$httpVersion $status$CRLF$CRLF".getBytes())

  } catch {
    case e: IOException =>
      println(s"IOException: ${e.getMessage}")
  }
}
