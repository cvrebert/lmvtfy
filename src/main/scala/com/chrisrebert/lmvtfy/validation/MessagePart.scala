package com.chrisrebert.lmvtfy.validation

sealed trait MessagePart
case class PlainText(text: String) extends MessagePart
case class Link(url: String, title: String) extends MessagePart
case class CodeText(text: String) extends MessagePart
