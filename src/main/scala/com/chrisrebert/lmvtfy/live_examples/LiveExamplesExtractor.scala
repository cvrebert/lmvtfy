package com.chrisrebert.lmvtfy.live_examples

import scala.util.Try
import spray.http.Uri
import com.chrisrebert.lmvtfy.util.RichUri

/**
 * Wrap the raw com.twitter.Extractor
 */
private object UrlExtractor {
  import scala.collection.JavaConverters._

  private val urlExtractor = new com.twitter.Extractor()
  def extractAllUrls(text: String) = urlExtractor.extractURLs(text).asScala.toSet[String]
}

object LiveExamplesExtractor {
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
        case PlunkerExample(plunk) => Some(plunk)
        case _ => None
      }
    }
  }
}
