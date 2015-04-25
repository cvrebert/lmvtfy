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
    }.filter{ uri => uri.isSafe }.map{ _.withoutFragment }
  }

  def liveExamplesFromWithin(text: String): Set[LiveExample] = {
    safeHttpUrlsFromWithin(text).flatMap{ LiveExample(_) }
  }
}
