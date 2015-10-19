package com.chrisrebert.lmvtfy.live_examples

import scala.util.{Try,Success,Failure}
import spray.json._
import com.chrisrebert.lmvtfy.live_examples.jsbin.JsBin
import com.chrisrebert.lmvtfy.live_examples.jsbin.JsBinJsonProtocol._
import com.chrisrebert.lmvtfy.util.ConvenientString

object JsBinUserHtmlFromStartJs {
  private val ScriptCodeRegex = "(?s)^start[(](.+)".r
  private val jsonUnicodeHexEscapedBackslash = "\\" + "u005C"
  private val jsonBackslash = "\\\\" // would be a literal backslash after the JSON string is decoded

  def unapply(binPageHtml: String): Option[String] = {
    binPageHtml match {
      case ScriptCodeRegex(funcall) => {
        val jsonish = "[" + funcall.thruFinal("}") + "]" // funcall contains two comma-separated JSON objects
        // JS Bin escapes exclamation points, probably to avoid interpretation as an HTML comment
        val json = jsonish.replaceAllLiterally(jsonBackslash, jsonUnicodeHexEscapedBackslash).replaceAllLiterally("\\!", "!")
        Try { json.parseJson } match {
          case Success(JsArray(Vector(contentJson, _*))) => {
            Try { contentJson.convertTo[JsBin] } match {
              case Failure(structureExc) => None
              case Success(jsBin) => Some(jsBin.html)
            }
          }
          case _ => None
        }
      }
      case _ => None
    }
  }
}
