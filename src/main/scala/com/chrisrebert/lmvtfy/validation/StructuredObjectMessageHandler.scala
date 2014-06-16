package com.chrisrebert.lmvtfy.validation

import scala.collection.mutable
import org.xml.sax.SAXException
import nu.validator.messages.MessageTextHandler
import com.chrisrebert.lmvtfy.util.RichStack

class StructuredObjectMessageHandler extends MessageTextHandler {
  private val stateStack = new mutable.Stack[State]().push(NeutralState)
  private val parts = new mutable.ListBuffer[MessagePart]()
  private var buffer = new StringBuffer()

  /**
   * @see nu.validator.messages.MessageTextHandler#characters
   */
  override def characters(ch: Array[Char], start: Int, length: Int) {
    buffer.append(ch)
  }

  /**
   * @throws IllegalStateException
   */
  def end() {
    val state = stateStack.popOption()
    if (!stateStack.isEmpty) {
      throw new IllegalStateException("Unexpected extra states left on stack")
    }

    state match {
      case Some(NeutralState) => {
        if (buffer.length > 0) {
          parts += PlainText(buffer.toString)
          buffer = null
        }
      }
      case Some(CodeState) => throw new IllegalStateException("Base state at end of handling should be NeutralState, not CodeState")
      case None => throw new IllegalStateException("State stack is empty, which should never happen")
    }
  }

  /**
   * @see nu.validator.messages.MessageTextHandler#startCode()
   * @throws SAXException
   */
  override def startCode() {
    stateStack.topOption match {
      case None => throw new SAXException("State stack is empty, which should never happen")
      case Some(CodeState) => throw new SAXException("Unexpected nested code")
      case Some(NeutralState) => {
        if (buffer.length > 0) {
          parts += PlainText(buffer.toString)
          buffer = new StringBuffer()
        }
      }
    }
    stateStack.push(CodeState)
  }

  /**
   * @see nu.validator.messages.MessageTextHandler#endCode()
   * @throws SAXException
   */
  override def endCode() {
    stateStack.popOption() match {
      case Some(CodeState) => {
        parts += CodeText(buffer.toString)
        buffer = new StringBuffer()
      }
      case Some(NeutralState) => throw new SAXException("Expected CodeState, got NeutralState")
      case None => throw new SAXException("Expected CodeState, but stack was empty")
    }
  }

  /**
   * @see nu.validator.messages.MessageTextHandler#startLink
   */
  override def startLink(href: String, title: String) {
    parts += Link(href, title)
  }

  /**
   * @see nu.validator.messages.MessageTextHandler#endLink()
   */
  override def endLink() {
    // deliberately do nothing
  }

  def message: Seq[MessagePart] = parts.to[Vector]
}

private sealed trait State
private object NeutralState extends State
private object CodeState extends State
