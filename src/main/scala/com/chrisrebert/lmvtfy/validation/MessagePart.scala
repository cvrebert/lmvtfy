package com.chrisrebert.lmvtfy.validation

sealed trait MessagePart
object PlainText {
  def apply(text: String): PlainText = {
    new PlainText(text)
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
