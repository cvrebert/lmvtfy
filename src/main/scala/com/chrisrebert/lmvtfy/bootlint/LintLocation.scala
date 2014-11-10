package com.chrisrebert.lmvtfy.bootlint

case class LintLocation(line: Int, column: Int) {
  override def toString = s"${line}:${column}"
}
