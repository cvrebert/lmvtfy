/*
 * Copyright (c) 2014 Christopher Rebert
 * Copyright (c) 2013-2014 Mozilla Foundation
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package com.chrisrebert.lmvtfy.validation

import scala.util.{Try,Success,Failure}
import nu.validator.messages._
import nu.validator.servlet.imagereview.ImageCollector
import nu.validator.source.SourceCode
import nu.validator.validation.SimpleDocumentValidator
import nu.validator.xml.SystemErrErrorHandler
import org.xml.sax.InputSource

// java -Xss512k
object Html5Validator {
  private val schemaUrl = "http://s.validator.nu/html5-rdfalite.rnc"
  private val errorsOnly = true
  private val showSource = false
  System.setProperty("org.whattf.datatype.warn", errorsOnly.toString)

  def validationErrorsFor(inputSource: InputSource): Try[Seq[ValidationMessage]] = {
    (new Html5Validator(inputSource)).validationErrors
  }
}

private class Html5Validator(inputSource: InputSource) {
  import Html5Validator.{schemaUrl,errorsOnly,showSource}

  private val emitter = new StructuredObjectEmitter()

  lazy val validationErrors: Try[Seq[ValidationMessage]] = {
    validator.checkHtmlInputSource(inputSource)
    end().flatMap{ _ =>
      Success(emitter.messages.filter{ msg =>
        msg.parts match {
          // Exempt <img>s without alt attributes as they typically don't cause any problems besides decreasing accessibility, and most live examples lack them due to irrelevance and extra effort
          case Seq(PlainText("An "), CodeText("img"), PlainText(" element must have an "), CodeText("alt"), _*) => false
          // Exempt missing <title> as it is very common in live examples but typically doesn't cause any problem
          case Seq(PlainText("Element "), CodeText("head"), PlainText(" is missing a required instance of child element "), CodeText("title"), PlainText(".")) => false
          // Exempt data-* attributes on non-HTML (e.g. SVG) elements as they typically don't cause any problems
          case Seq(PlainText("Attribute "), CodeText(attrName), PlainText(" not allowed on element "), _*) if attrName.startsWith("data-") => false
          // Exempt nonstandard <meta> used by jsFiddle
          case Seq(PlainText("Bad value "), CodeText("edit-Type"), PlainText(" for attribute "), CodeText("http-equiv"), PlainText(" on element "), CodeText("meta"), PlainText(".")) => false
          case Seq(PlainText("Attribute "), CodeText("edit"), PlainText(" not allowed on element "), CodeText("meta"), PlainText(" at this point.")) => false
          // Exempt nonstandard usage of autocomplete attribute because of Firefox bug: https://bugzilla.mozilla.org/show_bug.cgi?id=654072
          case Seq(PlainText("Attribute "), CodeText("autocomplete"), PlainText(" not allowed on element "), CodeText("input"), PlainText(" at this point.")) => false
          case Seq(PlainText("Attribute "), CodeText("autocomplete"), PlainText(" not allowed on element "), CodeText("button"), PlainText(" at this point.")) => false
          case _ => true
        }
      })
    }
  }

  /**
   * @throws SAXException, Exception
   * @throws SimpleDocumentValidator.SchemaReadException, StackOverflowError
   */
  private lazy val validator: SimpleDocumentValidator = {
    val simpleDocValidator = new SimpleDocumentValidator()
    simpleDocValidator.setUpMainSchema(schemaUrl, new SystemErrErrorHandler())

    val loadEntities = false
    val noStream = false
    simpleDocValidator.setUpValidatorAndParsers(errorHandler, noStream, loadEntities)

    simpleDocValidator
  }

  private lazy val errorHandler: MessageEmitterAdapter = {
    val lineOffset = 0
    val sourceCode = new SourceCode()
    val imageCollector = new ImageCollector(sourceCode)

    val errHandler = new MessageEmitterAdapter(sourceCode, showSource, imageCollector, lineOffset, true, emitter)
    errHandler.setHtml(true)
    errHandler.setErrorsOnly(errorsOnly)
    errHandler.start(null)
    errHandler
  }

  private def end(): Try[Unit] = Try{ errorHandler.end("Document checking completed. No errors found.", "Document checking completed.") }
}
