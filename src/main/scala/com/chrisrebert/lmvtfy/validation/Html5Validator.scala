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
  private val errorsOnly = false
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
          case Seq(PlainText("Bad value "), CodeText("X-UA-Compatible"), PlainText(" for attribute "), CodeText("http-equiv"), PlainText(" on HTML element "), CodeText("meta"), PlainText(".")) => false
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
    errHandler.setErrorsOnly(errorsOnly)
    errHandler
  }

  /**
   * @throws SAXException
   */
  private def end(): Try[Unit] = Try{ errorHandler.end("Document checking completed. No errors found.", "Document checking completed.") }
}
