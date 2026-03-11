package codecrafters_http_server.models

opaque type RepresentationHeader = String
object RepresentationHeader:
  val CONTENT_TYPE: RepresentationHeader = "content-type"
  val CONTENT_LENGTH: RepresentationHeader = "content-length"
  def fromString(s: String): RepresentationHeader = s

opaque type RequestHeader = String
object RequestHeader:
  val HOST: RequestHeader = "host"
  val USER_AGENT: RequestHeader = "user-agent"
  val ACCEPT: RequestHeader = "accept"
  def fromString(s: String): RequestHeader = s
