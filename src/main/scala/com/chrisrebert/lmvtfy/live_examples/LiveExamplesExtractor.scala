package com.chrisrebert.lmvtfy.live_examples

import scala.util.Try
import spray.http.Uri
import spray.http.Uri.NamedHost

/**
 * Wrap the raw com.twitter.Extractor
 */
private object UrlExtractor {
  import scala.collection.JavaConverters._

  private val urlExtractor = new com.twitter.Extractor()
  def extractAllUrls(text: String) = urlExtractor.extractURLs(text).asScala.toSet[String]
}

object LiveExamplesExtractor {
  private implicit class RichUri(uri: Uri) {
    import spray.http.Uri.Query.{Empty=>EmptyQuery}

    def isHttp = uri.scheme == "http" || uri.scheme == "https"
    def lacksUserInfo = uri.authority.userinfo.isEmpty
    def lacksNonDefaultPort = uri.authority.port <= 0
    def hasNamedHost = uri.authority.host.isInstanceOf[NamedHost]
    def isSafe = uri.isHttp && uri.lacksUserInfo && uri.hasNamedHost && uri.lacksNonDefaultPort && uri.isAbsolute
    def withoutQuery = uri.withQuery(EmptyQuery)
  }

  private def safeHttpUrlsFromWithin(text: String): Set[Uri] = {
    UrlExtractor.extractAllUrls(text).flatMap{ urlStr =>
      Try{ Uri(urlStr) }.toOption
    }.filter{ uri => uri.isSafe }.map{ _.withoutQuery.withoutFragment }
  }

  def liveExamplesFromWithin(text: String): Set[LiveExample] = {
    safeHttpUrlsFromWithin(text).flatMap{ url =>
      url match {
        case JsFiddleExample(fiddle) => Some(fiddle)
        case JsBinExample(bin) => Some(bin)
        case BootplyExample(ply) => Some(ply)
        case _ => None
      }
    }
  }
}

//FIXME: look for HTML code blocks
