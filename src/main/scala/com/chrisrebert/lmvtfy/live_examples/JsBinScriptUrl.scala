package com.chrisrebert.lmvtfy.live_examples

import scala.util.Try
import spray.http.Uri

object JsBinScriptUrl {
  private val ScriptSrcRegex = "(?s).*<script(?:\\s+)src=\"(http(?:s?)://jsbin.com/bin/start\\.js\\?[^\"]+)\">.*".r

  def unapply(binPageHtml: String): Option[Uri] = {
    binPageHtml match {
      case ScriptSrcRegex(scriptUrl) => Try{ Uri(scriptUrl) }.toOption
      case _ => None
    }
  }
}
