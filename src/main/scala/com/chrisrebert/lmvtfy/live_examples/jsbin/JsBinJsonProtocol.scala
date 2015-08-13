package com.chrisrebert.lmvtfy.live_examples.jsbin

import spray.json._

case class JsBin(html: String)

object JsBinJsonProtocol extends DefaultJsonProtocol {
  implicit val jsBinFormat = jsonFormat1(JsBin.apply)
}
