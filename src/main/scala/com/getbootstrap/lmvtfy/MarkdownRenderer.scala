package com.getbootstrap.lmvtfy

import com.getbootstrap.lmvtfy.validation._
import java.util.regex.Pattern

object MarkdownRenderer {
  implicit class MarkdownMessagePart(part: MessagePart) {
    private val uberCodeQuote = "````"
    private val tooManyBackticks = Pattern.compile(uberCodeQuote + "+")

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

  def markdownFor(validationMessages: Seq[ValidationMessage]): String = {
    validationMessages.map{ "* " + _.markdown }.mkString("\n")
  }
}
