package com.chrisrebert.lmvtfy.bootlint

case class BootlintProblem(id: String, message: String, location: Option[LintLocation]) {
  def explanationUrl: String = s"https://github.com/twbs/bootlint/wiki/${id}"
}
