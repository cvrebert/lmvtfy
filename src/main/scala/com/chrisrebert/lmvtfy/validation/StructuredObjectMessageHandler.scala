package com.chrisrebert.lmvtfy.validation

import scala.collection.mutable
import org.xml.sax.SAXException
import nu.validator.messages.MessageTextHandler

class StructuredObjectMessageHandler extends MessageTextHandler {
  private val stateStack = new mutable.Stack[State]().push(NeutralState)
  private val parts = new mutable.ListBuffer[MessagePart]()
  private var buffer = new StringBuffer()

  override def characters(ch: Array[Char], start: Int, length: Int) {
    buffer.append(ch)
  }
// FIXME: catch NoSuchElementException on pop
  /**
   * @throws SAXException
   */
  def end() {
    val state = stateStack.pop()
    if (!stateStack.isEmpty) {
      throw new SAXException("Unexpected extra states left on stack")
    }

    state match {
      case NeutralState => {
        if (buffer.length > 0) {
          parts += PlainText(buffer.toString)
          buffer = null
        }
      }
      case CodeState => throw new SAXException("Base state at end of handling should be NeutralState, not CodeState")
    }
  }

  override def startCode() {
    stateStack.top match {
      case CodeState => throw new SAXException("Unexpected nested code")
      case NeutralState => {
        if (buffer.length > 0) {
          parts += PlainText(buffer.toString)
          buffer = new StringBuffer()
        }
      }
    }
    stateStack.push(CodeState)
  }

  override def endCode() {
    stateStack.pop() match {
      case CodeState => {
        parts += CodeText(buffer.toString)
        buffer = new StringBuffer()
      }
      case NeutralState => throw new SAXException(s"Expected CodeState, got NeutralState")
    }
  }

  override def startLink(href: String, title: String) {
    parts += Link(href, title)
  }

  override def endLink() {
    // deliberately do nothing
  }

  def message: Seq[MessagePart] = parts.to[Vector]
}

private sealed trait State
private object NeutralState extends State
private object CodeState extends State
