package codecrafters_http_server.models

opaque type Encoding = String
object Encoding:
  val GZIP: Encoding = "gzip"

  def fromString(s: String): Encoding = s
  extension (e: Encoding) def value: String = e