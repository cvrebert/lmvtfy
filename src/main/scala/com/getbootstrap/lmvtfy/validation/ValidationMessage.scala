package com.getbootstrap.lmvtfy.validation

sealed case class ValidationMessage(
  start: Option[SourceLocation],
  end: Option[SourceLocation],
  parts: Seq[MessagePart]
)
