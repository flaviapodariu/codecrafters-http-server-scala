package codecrafters_http_server.models
import codecrafters_http_server.models.RequestHeader

case class RequestLine(
    verb: HttpVerb,
    path: String,
    httpVersion: HttpVersion
) {}

opaque type HttpVerb = String
object HttpVerb:
  val GET: HttpVerb = "GET"
  val POST: HttpVerb = "POST"

  def fromString(s: String): HttpVerb = s

opaque type HttpVersion = String
object HttpVersion:
  val HTTP_1_0: HttpVersion = "HTTP/1.0"
  val HTTP_1_1: HttpVersion = "HTTP/1.1"
  val HTTP_2: HttpVersion = "HTTP/2"
  val HTTP_3: HttpVersion = "HTTP/3"

  def fromString(s: String): HttpVersion = s

case class HttpRequest(
    line: RequestLine,
    headers: Map[RequestHeader, String] = Map.empty,
    body: String = ""
)

object HttpRequest:
  def parse(raw: List[String]): HttpRequest =
    val Array(verb, path, httpVersion) = raw.head.split(" ")
    val requestLine = RequestLine(
      HttpVerb.fromString(verb),
      path,
      HttpVersion.fromString(httpVersion)
    )

    val headers = raw
      .drop(1)
      .map(h => h.split(":", 2))
      .collect { case Array(k, v) =>
        RequestHeader.fromString(k.toLowerCase()) -> v
      }
      .toMap

    HttpRequest(requestLine, headers, "")
