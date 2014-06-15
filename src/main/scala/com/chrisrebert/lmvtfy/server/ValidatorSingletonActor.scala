package com.chrisrebert.lmvtfy.server

import java.io.ByteArrayInputStream
import org.xml.sax.InputSource
import akka.actor.ActorRef
import com.chrisrebert.lmvtfy.validation.Html5Validator
import com.chrisrebert.lmvtfy.MarkdownRenderer
import com.chrisrebert.lmvtfy.{ValidationRequest, ValidationResult}

class ValidatorSingletonActor(commenter: ActorRef) extends ActorWithLogging {
  override def receive = {
    case ValidationRequest(htmlBytes, mention) => {
      val htmlByteStream = new ByteArrayInputStream(htmlBytes.toArray)
      val htmlInputSource = new InputSource(htmlByteStream)
      val validationErrs = Html5Validator.validationErrorsFor(htmlInputSource)
      if (validationErrs.isEmpty) {
        log.info(s"No validation errors for ${mention}")
      }
      else {
        log.info(s"${validationErrs.length} validation errors for ${mention}")
        val validationMessagesAsMarkdown = MarkdownRenderer.markdownFor(validationErrs)
        commenter ! ValidationResult(validationMessagesAsMarkdown, mention)
      }
    }
  }
}
