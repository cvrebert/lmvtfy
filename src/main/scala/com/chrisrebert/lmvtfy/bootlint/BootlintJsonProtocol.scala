package com.chrisrebert.lmvtfy.bootlint

import spray.json._

object BootlintJsonProtocol extends DefaultJsonProtocol {
  implicit val locationFormat = jsonFormat2(LintLocation.apply)
  implicit val bootlintFormat = jsonFormat3(BootlintProblem.apply)
}
