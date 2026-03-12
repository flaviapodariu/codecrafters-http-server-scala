package codecrafters_http_server.models
import codecrafters_http_server.models.Header

import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets
import java.util.zip.{GZIPInputStream, GZIPOutputStream}

val CRLF = "\r\n"

case class HttpResponse(
    status: StatusCode,
    headers: Map[Header, String] = Map.empty,
    body: Array[Byte] = Array.empty
) {

  def toBytes(httpVersion: String): Array[Byte] =
    val statusLine = s"$httpVersion $status$CRLF"
    val headerSection = headers
      .map { case (k, v) => s"$k: $v" }
      .mkString(CRLF) + CRLF

    val headerBytes = headerSection.getBytes(StandardCharsets.UTF_8)


    s"$statusLine$headerSection$CRLF".getBytes() ++ body

  def withEncoding(encodings: Option[String]): HttpResponse =
    val clientEncodings = getSupportedEncodings(encodings)
    val serverSupported = List(Encoding.GZIP)
    val selectedEncoding = clientEncodings.find(enc => serverSupported.contains(enc))

    selectedEncoding match {
      case Some(Encoding.GZIP) =>
        val compressed = gzip(body)
        copy(
          headers = headers + (
            Header.CONTENT_ENCODING -> Encoding.GZIP.value,
            Header.CONTENT_LENGTH -> compressed.length.toString
            ),
          body = compressed
          )
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
