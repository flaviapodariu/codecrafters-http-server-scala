package codecrafters_http_server.models
import codecrafters_http_server.models.RepresentationHeader

val CRLF = "\r\n"

case class HttpResponse(
    status: StatusCode,
    headers: Map[RepresentationHeader, String] = Map.empty,
    body: String = ""
) {
  def buildHeaders(): Map[RepresentationHeader, String] =
    if (body.nonEmpty) {
      headers + (
        RepresentationHeader.CONTENT_TYPE -> "text/plain",
        RepresentationHeader.CONTENT_LENGTH -> body.length.toString
      )
    } else headers

  def toBytes(httpVersion: String): Array[Byte] =
    val statusLine = s"$httpVersion $status$CRLF"
    val headerSection = buildHeaders()
      .map { case (k, v) => s"$k: $v" }
      .mkString(CRLF)

    s"$statusLine$headerSection$CRLF$body".getBytes()
}

opaque type StatusCode = String
object StatusCode:
  val OK: StatusCode = "200 OK"
  val NOT_FOUND: StatusCode = "404 Not Found"
