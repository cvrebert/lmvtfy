package com.chrisrebert.lmvtfy

sealed trait Markdown {
  def markdown: String
}
case class MarkdownAboutHtml(markdown: String) extends Markdown
case class MarkdownAboutBootstrap(markdown: String) extends Markdown
