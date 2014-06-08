package com.getbootstrap.lmvtfy.validation

sealed trait MessagePart
object PlainText {
  def apply(text: String): PlainText = {
    new PlainText(XhtmlRedactor.redact(text))
  }
  def unapply(plainText: PlainText) = Some(plainText.text)
}
class PlainText private(val text: String) extends MessagePart {
  override def equals(other: Any) = other != null && other.isInstanceOf[PlainText] && other.asInstanceOf[PlainText].text == text
  override def hashCode = text.hashCode
  override def toString = s"PlainText(${text})"
}
case class Link(url: String, title: String) extends MessagePart
case class CodeText(text: String) extends MessagePart


object XhtmlRedactor {
  import java.util.regex.Pattern

  private val pattern = Pattern.compile("\\bXHTML\\b", Pattern.UNICODE_CHARACTER_CLASS)

  /**
   * Replaces all references to XHTML with HTML
   * @param str
   */
  def redact(str: String): String = pattern.matcher(str).replaceAll("HTML")
}
