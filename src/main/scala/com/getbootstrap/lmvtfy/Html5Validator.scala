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

package com.getbootstrap.lmvtfy

import nu.validator.json.{Serializer=>JsonSerializer}
import nu.validator.messages.{MessageEmitterAdapter,JsonMessageEmitter}
import nu.validator.servlet.imagereview.ImageCollector
import nu.validator.source.SourceCode
import nu.validator.validation.SimpleDocumentValidator

import org.xml.sax.InputSource
import org.xml.sax.SAXException
import org.xml.sax.SAXParseException

object Main {
    // java -Xss512k -jar ~/vnu.jar FILE.html

    // throws SAXException, Exception
    def main(args: Array[String]) {
        val is: InputSource = new InputSource(System.in)
        Html5Validator.validationErrorsFor(is)
    }
}

object Html5Validator {
    private val schemaUrl = "http://s.validator.nu/html5-rdfalite.rnc"
    private val showSource: Boolean = false
    private val errorsOnly: Boolean = false
    System.setProperty("org.whattf.datatype.warn", errorsOnly.toString)

    def validationErrorsFor(inputSource: InputSource): String = {
        (new Html5Validator(inputSource)).validationErrors
    }
}

class Html5Validator(inputSource: InputSource) {
    import Html5Validator.{schemaUrl,showSource,errorsOnly}

    private val outputStream = new java.io.ByteArrayOutputStream()

    // throws SAXException, IOException
    private lazy val validationErrors: String = {
        validator.checkHtmlInputSource(inputSource)
        end()
        // outputStream.toString("utf-8")
        // outputStream.toByteArray
        ""
    }

    // throws SAXException
    private def end() {
        errorHandler.end("Document checking completed. No errors found.", "Document checking completed.")
    }

    // throws SAXException, Exception
    // throws SimpleDocumentValidator.SchemaReadException, StackOverflowError
    private lazy val validator: SimpleDocumentValidator = {
        validator.setUpMainSchema(schemaUrl, errorHandler)

        val noStream: Boolean = false // FIXME: probably okay?
        val loadEntities: Boolean = false // FIXME: probably right?
        validator.setUpValidatorAndParsers(errorHandler, noStream, loadEntities)

        validator
    }

    private lazy val errorMessageEmitter = {
        val callback: String = null
        var serializer = new JsonSerializer(outputStream)
        new JsonMessageEmitter(serializer, callback)
    }

    private lazy val errorHandler = {
        val lineOffset: Int = 0
        val sourceCode: SourceCode = new SourceCode()
        val imageCollector: ImageCollector = new ImageCollector(sourceCode)

        val errHandler = new MessageEmitterAdapter(sourceCode, showSource, imageCollector, lineOffset, true, errorMessageEmitter)
        errHandler.setErrorsOnly(errorsOnly)
        errHandler
    }
}
