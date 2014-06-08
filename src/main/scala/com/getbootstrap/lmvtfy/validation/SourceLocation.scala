package com.getbootstrap.lmvtfy.validation

object SourceLocation {
  def apply(lineNum: Int, columnNum: Int): Option[SourceLocation] = {
    if (lineNum <= 0) {
      None
    }
    else {
      val column = if (columnNum <= 0) None else Some(columnNum)
      Some(new SourceLocation(lineNum, column))
    }
  }
}
sealed case class SourceLocation(lineNum: Int, columnNum: Option[Int])
