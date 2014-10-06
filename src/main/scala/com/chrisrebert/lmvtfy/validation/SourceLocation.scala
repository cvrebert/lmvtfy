package com.chrisrebert.lmvtfy.validation

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
sealed case class SourceLocation private(lineNum: Int, columnNum: Option[Int]) extends Ordered[SourceLocation] {
  override def toString: String = {
    val colPart = columnNum.map{ col => s"column ${col}" }
    s"line ${lineNum}" + colPart.map{ ", " + _ }.getOrElse("")
  }

  override def compare(that: SourceLocation) = {
    if (this.lineNum != that.lineNum) {
      this.lineNum - that.lineNum
    }
    else {
      this.columnNum.getOrElse(-1) - that.columnNum.getOrElse(-1)
    }
  }
}
