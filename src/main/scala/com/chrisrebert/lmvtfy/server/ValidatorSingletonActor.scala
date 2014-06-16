package com.chrisrebert.lmvtfy.server

import java.io.ByteArrayInputStream
import org.xml.sax.InputSource
import scala.util.{Success,Failure}
import akka.actor.ActorRef
import com.chrisrebert.lmvtfy.validation.Html5Validator
import com.chrisrebert.lmvtfy.MarkdownRenderer
import com.chrisrebert.lmvtfy.{ValidationRequest, ValidationResult}

class ValidatorSingletonActor(commenter: ActorRef) extends ActorWithLogging {
  override def receive = {
    case ValidationRequest(htmlBytes, mention) => {
      val htmlByteStream = new ByteArrayInputStream(htmlBytes.toArray)
      val htmlInputSource = new InputSource(htmlByteStream)
      Html5Validator.validationErrorsFor(htmlInputSource) match {
        case Failure(exc) => log.error(exc, s"HTML5 validator threw an exception for ${mention}")
        case Success(validationErrs) => {
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
  }
}
