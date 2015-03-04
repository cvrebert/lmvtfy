package com.chrisrebert.lmvtfy.util

object IsHtmlish {
  private val startTag = "<[a-zA-Z]+>".r.unanchored
  private val endTag = "</[a-zA-Z]+>".r.unanchored

  def unapply(maybeHtml: String): Boolean = apply(maybeHtml)
  def apply(maybeHtml: String): Boolean = (maybeHtml matches startTag) && (maybeHtml matches endTag)
}
