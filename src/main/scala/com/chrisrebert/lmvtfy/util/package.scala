package com.chrisrebert.lmvtfy

import java.nio.charset.Charset
import scala.collection.mutable
import scala.util.Try
import akka.util.ByteString
import spray.http.{Uri, ContentType, MediaTypes, HttpCharsets, HttpEntity, HttpResponse}

package object util {
  private val utf8name = "UTF-8"
  private val utf8Charset = Charset.forName(utf8name)

  implicit class Utf8String(str: String) {
    def utf8Bytes: Array[Byte] = str.getBytes(utf8Charset)
    def utf8ByteString: ByteString = ByteString(this.utf8Bytes)
  }

  implicit class ConvenientString(str: String) {
    def rindex(substr: String): Option[Int] = {
      str.lastIndexOf(substr) match {
        case -1 => None
        case validIndex => Some(validIndex)
      }
    }

    def thruFinal(substr: String): String = {
      str.rindex(substr) match {
        case None => str
        case Some(index) => str.substring(0, index + 1)
      }
    }

    def matches(regex: scala.util.matching.Regex): Boolean = {
      str match {
        case regex(_*) => true
        case _ => false
      }
    }
  }

  implicit class InputSourceString(str: String) {
    import java.io.StringReader
    import org.xml.sax.InputSource

    def asInputSource: InputSource = new InputSource(new StringReader(str))
  }

  implicit class Utf8ByteArray(bytes: Array[Byte]) {
    def utf8String: Try[String] = Try { new String(bytes, utf8Charset) }
  }

  implicit class Utf8ByteString(byteStr: ByteString) {
    def utf8String: String = byteStr.decodeString(utf8name)
    def asUtf8HtmlHttpEntity: HttpEntity = HttpEntity(ContentType(MediaTypes.`text/html`, HttpCharsets.`UTF-8`), byteStr)
  }

  implicit class RichResponse(response: HttpResponse) {
    def entityByteString: ByteString = response.entity.data.toByteString
    def entityUtf8String: String = response.entity.data.asString(utf8Charset)
  }

  implicit class RichStack[T](stack: mutable.Stack[T]) {
    def popOption(): Option[T] = Try{ stack.pop() }.toOption
    def topOption: Option[T] = Try{ stack.top }.toOption
  }

  implicit class RichUri(uri: Uri) {
    import spray.http.Uri.NamedHost
    import spray.http.Uri.Query.{Empty=>EmptyQuery}

    def isHttp = uri.scheme == "http" || uri.scheme == "https"
    def lacksUserInfo = uri.authority.userinfo.isEmpty
    def lacksNonDefaultPort = uri.authority.port <= 0
    def hasNamedHost = uri.authority.host.isInstanceOf[NamedHost]
    def isSafe = uri.isHttp && uri.lacksUserInfo && uri.hasNamedHost && uri.lacksNonDefaultPort && uri.isAbsolute
    def withoutQuery = uri.withQuery(EmptyQuery)
  }

  object HtmlSuffixed {
    private val extension = ".html"
    def unapply(filename: String): Option[String] = Some(if (filename.endsWith(extension)) filename else (filename + extension))
  }
}
