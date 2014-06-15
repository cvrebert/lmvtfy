package com.chrisrebert.lmvtfy.github

object IssueNumber {
  def apply(number: Int): Option[IssueNumber] = {
    if (number > 0) {
      Some(new IssueNumber(number))
    }
    else {
      None
    }
  }
}
class IssueNumber private(val number: Int) extends AnyVal {
  override def toString = s"IssueNumber(${number})"
}
