package codecrafters_http_server.models
import codecrafters_http_server.models.Header

val CRLF = "\r\n"

case class HttpResponse(
    status: StatusCode,
    headers: Map[Header, String] = Map.empty,
    body: String = ""
) {

  def toBytes(httpVersion: String): Array[Byte] =
    val statusLine = s"$httpVersion $status$CRLF"
    val headerSection = headers
      .map { case (k, v) => s"$k: $v" }
      .mkString(CRLF) + CRLF

    s"$statusLine$headerSection$CRLF$body".getBytes()

  def withEncoding(encodings: Option[String]): HttpResponse =
    val clientEncodings = getSupportedEncodings(encodings)
    val serverSupported = List(Encoding.GZIP)
    val selectedEncoding = clientEncodings.find(enc => serverSupported.contains(enc))

    selectedEncoding match {
      case Some(enc) =>
        copy(headers = headers + (Header.CONTENT_ENCODING -> enc.value))
      case None =>
        this
    }

}

opaque type StatusCode = String
object StatusCode:
  val OK: StatusCode = "200 OK"
  val CREATED: StatusCode = "201 Created"
  val NOT_FOUND: StatusCode = "404 Not Found"
  val NOT_IMPLEMENTED: StatusCode = "501 Not Implemented"
