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

// import org.xml.sax.InputSource
// import nu.validator.validation.SimpleDocumentValidator

import java.io.File
import java.io.InputStream
import java.io.IOException
import java.io.OutputStream
import java.net.URL
import java.util.ArrayList
import java.util.List

import nu.validator.htmlparser.sax.XmlSerializer
import nu.validator.messages.GnuMessageEmitter
import nu.validator.messages.JsonMessageEmitter
import nu.validator.messages.MessageEmitterAdapter
import nu.validator.messages.TextMessageEmitter
import nu.validator.messages.XmlMessageEmitter
import nu.validator.servlet.imagereview.ImageCollector
import nu.validator.source.SourceCode
import nu.validator.validation.SimpleDocumentValidator
import nu.validator.xml.SystemErrErrorHandler

import org.xml.sax.InputSource
import org.xml.sax.SAXException
import org.xml.sax.SAXParseException

sealed trait OutputFormat
case object HTML extends OutputFormat
case object XHTML extends OutputFormat
case object TEXT extends OutputFormat
case object XML extends OutputFormat
case object JSON extends OutputFormat
case object RELAXED extends OutputFormat
case object SOAP extends OutputFormat
case object UNICORN extends OutputFormat
case object GNU extends OutputFormat


object SimpleCommandLineValidator {
    private var validator: SimpleDocumentValidator = null

    private val out: OutputStream = System.err

    private var errorHandler: MessageEmitterAdapter = null

    private val verbose: Boolean = false

    private var errorsOnly: Boolean = false

    private val loadEntities: Boolean = false // FIXME: probably right?

    private val noStream: Boolean = false // FIXME: probably okay?

    private var forceHTML: Boolean = true

    private var asciiQuotes: Boolean = false // FIXME??

    private val lineOffset: Int = 0

    private val outputFormat: OutputFormat = JSON

    private val showSource: Boolean = false

    private val schemaUrl = "http://s.validator.nu/html5-rdfalite.rnc"

    // throws SAXException, Exception
    def main(args: Array[String]) = {
        System.setProperty("org.whattf.datatype.warn", errorsOnly.toString)
        if (false) {
            val is: InputSource = new InputSource(System.in)
            validator = new SimpleDocumentValidator()
            setup(schemaUrl)
            validator.checkHtmlInputSource(is)
            end()
        } else if (false) {
            val files: List[File] = new ArrayList[File]()
            args.foreach{ (p) => files.add(new File(p)) }
            validator = new SimpleDocumentValidator()
            setup(schemaUrl)
            checkFiles(files)
            end()
        } else {
            System.err.printf("\nError: No documents specified.\n")
            System.exit(-1)
        }
    }

    // throws SAXException, Exception
    // throws SimpleDocumentValidator.SchemaReadException, StackOverflowError
    // java -Xss512k -jar ~/vnu.jar FILE.html
    private def setup(schemaUrl: String) {
        setErrorHandler()
        validator.setUpMainSchema(schemaUrl, new SystemErrErrorHandler())
        validator.setUpValidatorAndParsers(errorHandler, noStream, loadEntities)
    }

    // throws SAXException
    private def end() {
        errorHandler.end("Document checking completed. No errors found.", "Document checking completed.")
    }

    // throws SAXException, IOException
    private def checkFiles(files: List[File]) {
        import scala.collection.JavaConverters._
        files.asScala.foreach(checkHtmlFile _)
    }

    // throws IOException
    private def checkHtmlFile(file: File) {
        try {
            validator.checkHtmlFile(file, true)
        } catch {
            case e: SAXException => {
                if (!errorsOnly) {
                    System.err.printf("\"%s\":-1:-1: warning: %s\n",
                            file.toURI().toURL().toString(), e.getMessage())
                }
            }
        }
    }

    private def setErrorHandler() {
        val sourceCode: SourceCode = new SourceCode()
        val imageCollector: ImageCollector = new ImageCollector(sourceCode)
        if (outputFormat == TEXT) {
            errorHandler = new MessageEmitterAdapter(sourceCode, showSource,
                    imageCollector, lineOffset, true, new TextMessageEmitter(
                            out, asciiQuotes))
        } else if (outputFormat == GNU) {
            errorHandler = new MessageEmitterAdapter(sourceCode, showSource,
                    imageCollector, lineOffset, true, new GnuMessageEmitter(
                            out, asciiQuotes))
        } else if (outputFormat == XML) {
            errorHandler = new MessageEmitterAdapter(sourceCode, showSource,
                    imageCollector, lineOffset, true, new XmlMessageEmitter(
                            new XmlSerializer(out)))
        } else if (outputFormat == JSON) {
            val callback: String = null
            errorHandler = new MessageEmitterAdapter(sourceCode, showSource,
                    imageCollector, lineOffset, true, new JsonMessageEmitter(
                            new nu.validator.json.Serializer(out), callback))
        } else {
            throw new RuntimeException("Bug. Should be unreachable.")
        }
        errorHandler.setErrorsOnly(errorsOnly)
    }
}