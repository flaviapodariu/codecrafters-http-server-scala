package codecrafters_http_server.models

import codecrafters_http_server.models
import codecrafters_http_server.models.Header.USER_AGENT

import java.nio.file.attribute.FileAttribute
import java.nio.file.{Path, Paths}

trait Endpoint:
  def execute(request: HttpRequest): HttpResponse


case class Home() extends Endpoint:
  def execute(request: HttpRequest): HttpResponse = {
    request.line.verb match
      case HttpVerb.GET => HttpResponse(StatusCode.OK)
      case _ => HttpResponse(StatusCode.NOT_IMPLEMENTED)
  }

case class Echo(path: String) extends Endpoint:
  def execute(request: HttpRequest): HttpResponse =
    request.line.verb match
      case HttpVerb.GET =>
        val message = path.stripPrefix("/echo/")
        HttpResponse(
          StatusCode.OK,
          headers = Map.apply(
            Header.CONTENT_TYPE -> "text/plain",
            Header.CONTENT_LENGTH -> message.length.toString
          ),
          body = message
        )
      case _ => HttpResponse(StatusCode.NOT_IMPLEMENTED)

case class UserAgent() extends Endpoint:
  def execute(request: HttpRequest): HttpResponse =
    request.line.verb match
      case HttpVerb.GET =>
        val ua = request.headers.getOrElse(USER_AGENT, "")
        HttpResponse(
          StatusCode.OK,
          headers = Map.apply(
            Header.CONTENT_TYPE -> "text/plain",
            Header.CONTENT_LENGTH -> ua.length.toString
          ),
          body = ua
        )
      case _ => HttpResponse(StatusCode.NOT_IMPLEMENTED)

case class Files(directory: String, uriPath: String) extends Endpoint:
  def execute(request: HttpRequest): HttpResponse =
    val fileName = uriPath.stripPrefix("/files/")
    request.line.verb match
      case HttpVerb.GET =>
        findFile(directory, fileName) match
          case Some(file) =>
            val size = file.toFile.length()
            HttpResponse(
              StatusCode.OK,
              headers = Map.apply(
                Header.CONTENT_TYPE -> "application/octet-stream",
                Header.CONTENT_LENGTH -> size.toString
              ),
              body = java.nio.file.Files.readString(file)
            )

          case None => HttpResponse(StatusCode.NOT_FOUND)

      case HttpVerb.POST =>
        val path = Paths.get(directory, fileName)
        try {
          val newFile = java.nio.file.Files.writeString(path, request.body)
        } catch {
          case e: Exception => println("Cannot create")
        }
        HttpResponse(StatusCode.CREATED)

      case _ => HttpResponse(StatusCode.NOT_IMPLEMENTED)


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
