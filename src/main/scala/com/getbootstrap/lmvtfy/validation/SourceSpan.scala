package com.getbootstrap.lmvtfy.validation

object SourceSpan {
  def apply(startLineNum: Int, startColumnNum: Int, endLineNum: Int, endColumnNum: Int): Option[SourceSpan] = {
    val start = SourceLocation(startLineNum, startColumnNum)
    val end = SourceLocation(endLineNum, endColumnNum)
    start match {
      case None => {
        end match {
          case None => None // nothing whatsoever
          case Some(only) => Some(new SourceSpan(only, None)) // end was really start
        }
      }
      case Some(someStart) => {
        end match {
          case None => Some(new SourceSpan(someStart, None)) // start only
          case Some(someEnd) => {
            if (someStart < someEnd) {
              // order already correct
              Some(new SourceSpan(someStart, end))
            }
            else if (someStart == someEnd) {
              // simplify span of 0/1 chars
              Some(new SourceSpan(someStart, None))
            }
            else {
              // need to flip
              val realStart = someEnd
              val realEnd = someStart
              Some(new SourceSpan(realStart, Some(realEnd)))
            }
          }
        }
      }
    }
  }
}
sealed case class SourceSpan private(start: SourceLocation, end: Option[SourceLocation]) {
  override def toString: String = s"${start}" + end.map{ s" thru " + _ }.getOrElse("")
}
