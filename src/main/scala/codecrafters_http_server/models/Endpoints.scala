package codecrafters_http_server.models

import codecrafters_http_server.models
import codecrafters_http_server.models.RequestHeader.USER_AGENT

import java.io.File
import java.nio.file.{Path, Paths}

trait Endpoint:
  def execute(request: HttpRequest): HttpResponse

case class Home() extends Endpoint:
  def execute(request: HttpRequest): HttpResponse = HttpResponse(StatusCode.OK)

case class Echo(path: String) extends Endpoint:
  def execute(request: HttpRequest): HttpResponse = {
    val message = path.stripPrefix("/echo/")
    HttpResponse(
      StatusCode.OK,
      headers = Map.apply(
        RepresentationHeader.CONTENT_TYPE -> "text/plain",
        RepresentationHeader.CONTENT_LENGTH -> message.length.toString
      ),
      body = message
    )
  }

case class UserAgent() extends Endpoint:
  def execute(request: HttpRequest): HttpResponse =
    val ua = request.headers.getOrElse(USER_AGENT, "")
    HttpResponse(
      StatusCode.OK,
      headers = Map.apply(
        RepresentationHeader.CONTENT_TYPE -> "text/plain",
        RepresentationHeader.CONTENT_LENGTH -> ua.length.toString
      ),
      body = ua
    )

case class Files(directory: String, path: String) extends Endpoint:
  def execute(request: HttpRequest): HttpResponse = {
    val fileName = path.stripPrefix("/files/")
    findFile(directory, fileName) match
      case Some(file) =>
        val size = file.toFile.length()
        HttpResponse(
          StatusCode.OK,
          headers = Map.apply(
            RepresentationHeader.CONTENT_TYPE -> "application/octet-stream",
            RepresentationHeader.CONTENT_LENGTH -> size.toString
          ),
          body = java.nio.file.Files.readString(file)
        )

      case None => HttpResponse(StatusCode.NOT_FOUND)
  }

  def findFile(dir: String, fileName: String): Option[Path] = {
    val path = Paths.get(dir, fileName)
    if (
      java.nio.file.Files
        .exists(path) && java.nio.file.Files.isRegularFile(path)
    ) {
      Some(path)
    } else
      None
  }
