package com.chrisrebert.lmvtfy.validation.markdown

import com.chrisrebert.lmvtfy.validation.ValidationMessage

object MarkdownRenderer {
  def markdownFor(validationMessages: Seq[ValidationMessage]): String = {
    validationMessages.map{ "* " + _.markdown }.mkString("\n")
  }
}
