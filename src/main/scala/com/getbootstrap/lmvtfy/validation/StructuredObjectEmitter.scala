package com.getbootstrap.lmvtfy.validation

import scala.collection.mutable
import nu.validator.messages.MessageTextHandler
import nu.validator.messages.types.MessageType

class StructuredObjectEmitter() extends nu.validator.messages.MessageEmitter {
  // FIXME: catch NoSuchElementException on pop
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

  override def endText() {
    handler.end()
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
    val start = SourceLocation(oneBasedFirstLine, oneBasedFirstColumn)
    val end = SourceLocation(oneBasedLastLine, oneBasedLastColumn)
    val flatMsgType = msgType.getFlatType // FIXME
    messagesStack.push(new ValidationMessage(start, end, Nil))
  }

  /**
   * @see nu.validator.messages.MessageEmitter#endMessage()
   */
  override def endMessage() {
    val blankMsg = messagesStack.pop()
    val fullMsg = new ValidationMessage(blankMsg.start, blankMsg.end, handler.message)
    messagesStack.push(fullMsg)
  }

  /*
  public nu.validator.messages.ResultHandler startResult() throws org.xml.sax.SAXException { /* compiled code */ }
  public void endResult() throws org.xml.sax.SAXException { /* compiled code */ }
  */
}
