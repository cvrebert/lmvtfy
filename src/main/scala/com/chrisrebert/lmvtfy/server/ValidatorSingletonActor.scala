package com.chrisrebert.lmvtfy.server

import java.io.ByteArrayInputStream
import org.xml.sax.InputSource
import scala.util.{Success,Failure}
import akka.actor.ActorRef
import com.chrisrebert.lmvtfy.validation.{MarkdownRenderer, Html5Validator}
import com.chrisrebert.lmvtfy.{MarkdownAboutHtml, ValidationRequest, ValidationResult}

class ValidatorSingletonActor(maybeBootlinter: Option[ActorRef], commenter: ActorRef) extends ActorWithLogging {
  override def receive = {
    case req@ValidationRequest(htmlBytes, mention) => {
      val htmlByteStream = new ByteArrayInputStream(htmlBytes.toArray)
      val htmlInputSource = new InputSource(htmlByteStream)
      Html5Validator.validationErrorsFor(htmlInputSource) match {
        case Failure(exc) => log.error(exc, s"HTML5 validator threw an exception for ${mention}")
        case Success(validationErrs) => {
          if (validationErrs.isEmpty) {
            log.info(s"No validation errors for ${mention}")
            maybeBootlinter.map{ _ ! req }
          }
          else {
            log.info(s"${validationErrs.length} validation errors for ${mention}")
            val validationMessagesAsMarkdown = MarkdownAboutHtml(MarkdownRenderer.markdownFor(validationErrs))
            commenter ! ValidationResult(validationMessagesAsMarkdown, mention)
          }
        }
      }
    }
  }
}
