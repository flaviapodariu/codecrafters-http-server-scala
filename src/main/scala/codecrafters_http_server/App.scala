package codecrafters_http_server

import java.io.IOException
import java.net.ServerSocket

object Main extends App {
  // You can use print statements as follows for debugging, they'll be visible when running tests.
  System.err.println("Logs from your program will appear here!")

  try {
    val serverSocket = new ServerSocket(4221)

    serverSocket.setReuseAddress(true)

    serverSocket.accept()
    println("accepted new connection")
  } catch {
    case e: IOException =>
      println(s"IOException: ${e.getMessage}")
  }
}
