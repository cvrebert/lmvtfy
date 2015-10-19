package com.chrisrebert.lmvtfy.validation

import java.util.regex.Pattern

package object markdown {
  private object MarkdownMessagePart {
    private val uberCodeQuote = "````"
    private val tooManyBackticks = Pattern.compile(uberCodeQuote + "+")
  }
  implicit class MarkdownMessagePart(part: MessagePart) {
    import MarkdownMessagePart._

    def markdown: String = {
      // escape backticks
      part match {
        case PlainText(plain) => plain.replace("`", "\\`")
        case CodeText(code) => {
          val sanitized = tooManyBackticks.matcher(code).replaceAll("[backticks]")
          uberCodeQuote + s" ${sanitized} " + uberCodeQuote
        }
        case _:Link => "" // ignoring links for now
      }
    }
  }

  implicit class MarkdownValidationMessage(msg: ValidationMessage) {
    def markdown: String = {
      msg.locationSpan.map{ _.toString + ": " }.getOrElse("") + msg.parts.map{ _.markdown }.mkString
    }
  }
}
