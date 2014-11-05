package com.chrisrebert.lmvtfy.bootlint

import spray.json._

object BootlintJsonProtocol extends DefaultJsonProtocol {
  implicit val bootlintFormat = jsonFormat2(BootlintProblem.apply)
}
