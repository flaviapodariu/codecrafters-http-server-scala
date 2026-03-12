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

  def withEncoding(encoding: Option[String]): HttpResponse =
    encoding match
      case Some(enc) if enc.contains(Encoding.GZIP.value) =>
        copy(headers = headers + (Header.CONTENT_ENCODING -> enc))
      case _ => this
}

opaque type StatusCode = String
object StatusCode:
  val OK: StatusCode = "200 OK"
  val CREATED: StatusCode = "201 Created"
  val NOT_FOUND: StatusCode = "404 Not Found"
  val NOT_IMPLEMENTED: StatusCode = "501 Not Implemented"
