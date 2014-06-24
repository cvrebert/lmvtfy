package com.chrisrebert.lmvtfy.validation

sealed trait MessagePart
object PlainText {
  def apply(text: String): PlainText = {
    new PlainText(SpellingErrorCorrector.correct(text))
  }
  def unapply(plainText: PlainText) = Some(plainText.text)
}
class PlainText private(val text: String) extends MessagePart {
  override def equals(other: Any) = other.isInstanceOf[PlainText] && other.asInstanceOf[PlainText].text == text
  override def hashCode = text.hashCode
  override def toString = s"PlainText(${text})"
}
case class Link(url: String, title: String) extends MessagePart
case class CodeText(text: String) extends MessagePart




object SpellingErrorCorrector {
  import java.util.regex.Pattern

  private val attrPattern = Pattern.compile("^Aattribute\\b", Pattern.UNICODE_CHARACTER_CLASS)
  private val elemPattern = Pattern.compile("^Eelement\\b", Pattern.UNICODE_CHARACTER_CLASS)

  /**
   * Corrects known spelling errors in the validator's messages
   * @param str
   */
  def correct(str: String): String = correctElement(correctAttribute(str))
  private def correctAttribute(str: String) = attrPattern.matcher(str).replaceFirst("Attribute")
  private def correctElement(str: String) = elemPattern.matcher(str).replaceFirst("Element")
}
