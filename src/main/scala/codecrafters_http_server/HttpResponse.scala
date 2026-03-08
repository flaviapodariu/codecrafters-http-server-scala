package codecrafters_http_server

val CRLF = "\r\n"

case class HttpResponse(
    status: StatusCode,
    headers: Map[HttpHeader, String] = Map.empty,
    body: String = ""
) {
  def buildHeaders(): Map[HttpHeader, String] =
    if (body.nonEmpty) {
      headers + (
        HttpHeader.CONTENT_TYPE -> "text/plain",
        HttpHeader.CONTENT_LENGTH -> body.length.toString
      )
    } else headers

  def toBytes(httpVersion: String): Array[Byte] =
    val statusLine = s"$httpVersion $status$CRLF"
    val headerSection = buildHeaders()
      .map { case (k, v) => s"$k: $v" }
      .mkString(CRLF) + CRLF

    s"$statusLine$headerSection$CRLF$body".getBytes()
}

opaque type StatusCode = String
object StatusCode:
  val OK: StatusCode = "200 OK"
  val NOT_FOUND: StatusCode = "404 Not Found"

opaque type HttpHeader = String
object HttpHeader:
  val CONTENT_TYPE: HttpHeader = "Content-Type"
  val CONTENT_LENGTH: HttpHeader = "Content-Length"
