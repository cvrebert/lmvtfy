package com.chrisrebert.lmvtfy.validation

import scala.collection.mutable
import org.xml.sax.SAXException
import nu.validator.messages.MessageTextHandler
import nu.validator.messages.types.MessageType
import com.chrisrebert.lmvtfy.util.RichStack

class StructuredObjectEmitter() extends nu.validator.messages.MessageEmitter {
  private val messagesStack = new mutable.Stack[ValidationMessage]()
  def messages: Seq[ValidationMessage] = messagesStack.to[Vector].reverse

  /**
   * @see nu.validator.messages.MessageEmitter#startMessages(java.lang.String)
   */
  override def startMessages(documentUri: String, willShowSource: Boolean) {
    // Nothing to do
  }

  /**
   * @see nu.validator.messages.MessageEmitter#endMessages()
   */
  override def endMessages() {
    // Nothing to do
  }

  private var handler: StructuredObjectMessageHandler = null

  /**
   * @see nu.validator.messages.MessageEmitter#startText()
   */
  override def startText: MessageTextHandler = {
    handler = new StructuredObjectMessageHandler()
    handler
  }

  /**
   * @see nu.validator.messages.MessageEmitter#startText()
   * @throws SAXException
   */
  override def endText() {
    try {
      handler.end()
    }
    catch {
      case stateExc: IllegalStateException => throw new SAXException("In illegal state when trying to end text of message", stateExc)
    }
  }

  /**
   * @see nu.validator.messages.MessageEmitter#startMessage()
   */
  override def startMessage(
    msgType: MessageType,
    systemId: String,
    oneBasedFirstLine: Int, oneBasedFirstColumn: Int,
    oneBasedLastLine: Int, oneBasedLastColumn: Int,
    exact: Boolean)
  {
    val locationSpan = SourceSpan(oneBasedFirstLine, oneBasedFirstColumn, oneBasedLastLine, oneBasedLastColumn)
    val flatMsgType = msgType.getFlatType // FIXME
    messagesStack.push(new ValidationMessage(locationSpan, Nil))
  }

  /**
   * @see nu.validator.messages.MessageEmitter#endMessage()
   * @throws SAXException
   */
  override def endMessage() {
    messagesStack.popOption() match {
      case None => throw new SAXException("No message was in progress")
      case Some(blankMsg) => {
        val fullMsg = new ValidationMessage(blankMsg.locationSpan, handler.message)
        messagesStack.push(fullMsg)
      }
    }
  }

  /*
  public nu.validator.messages.ResultHandler startResult() throws org.xml.sax.SAXException { ... }
  public void endResult() throws org.xml.sax.SAXException { ... }
  */
}
