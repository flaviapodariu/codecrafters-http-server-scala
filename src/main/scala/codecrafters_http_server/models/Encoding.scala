package codecrafters_http_server.models

opaque type Encoding = String
object Encoding:
  val GZIP: Encoding = "gzip"

  def fromString(s: String): Encoding = s
  extension (e: Encoding) def value: String = e

def getSupportedEncodings(encodings: Option[String]): List[Encoding] =
  encodings match
    case Some(values) =>
      values
        .split(",")
        .map(_.trim.toLowerCase)
        .map(c => Encoding.fromString(c))
        .toList
    case None => List.empty
