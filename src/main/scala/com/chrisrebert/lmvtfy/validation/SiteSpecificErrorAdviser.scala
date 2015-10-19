package com.chrisrebert.lmvtfy.validation

import com.chrisrebert.lmvtfy.live_examples.{LiveExample, JsFiddleExample}

object SiteSpecificErrorAdviser {
  def extraMessagesFor(
    example: LiveExample,
    htmlValidationMsgs: Seq[ValidationMessage]
  ): Seq[ValidationMessage] = {
    example match {
      case _:JsFiddleExample => {
        htmlValidationMsgs.headOption.map{ _.parts } match {
          case Some(Seq(PlainText("Stray start tag "), CodeText("html"), PlainText("."))) => {
            Seq(ValidationMessage(None, Seq(
              PlainText("JSFiddle inserts the contents of its \"HTML\" pane within "),
              CodeText("<body>...</body>"),
              PlainText(" tags, so you cannot include your own "),
              CodeText("<html>"), PlainText(", "), CodeText("<head>"), PlainText(", or "), CodeText("<body>"),
            PlainText(" tags in your JSFiddle.")
            )))
          }
          case Some(Seq(PlainText("Start tag "), CodeText("body"), PlainText(" seen but an element of the same type was already open."))) => {
            Seq(ValidationMessage(None, Seq(
              PlainText("JSFiddle inserts the contents of its \"HTML\" pane within "),
              CodeText("<body>...</body>"),
              PlainText(" tags, so you cannot include your own "),
              CodeText("<body>"),
              PlainText(" tag in your JSFiddle.")
            )))
          }
          case _ => Nil
        }
      }
      case _ => Nil
    }
  }
}
