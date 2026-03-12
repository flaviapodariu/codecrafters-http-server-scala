package codecrafters_http_server.models

opaque type Header = String
object Header:
  val CONTENT_TYPE: Header = "content-type"
  val CONTENT_LENGTH: Header = "content-length"
  val HOST: Header = "host"
  val USER_AGENT: Header = "user-agent"
  val ACCEPT: Header = "accept"
  val ACCEPT_ENCODING: Header = "accept-encoding"
  val CONTENT_ENCODING: Header = "content-encoding"
  def fromString(s: String): Header = s
