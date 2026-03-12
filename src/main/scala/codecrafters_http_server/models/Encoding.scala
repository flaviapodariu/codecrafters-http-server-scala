package codecrafters_http_server.models

import java.io.ByteArrayOutputStream
import java.util.zip.GZIPOutputStream

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
    
def gzip(body: Array[Byte]): Array[Byte] =
  val bos = new ByteArrayOutputStream(body.length)
  val gzip = new GZIPOutputStream(bos)
  gzip.write(body)
  gzip.close()
  bos.toByteArray
