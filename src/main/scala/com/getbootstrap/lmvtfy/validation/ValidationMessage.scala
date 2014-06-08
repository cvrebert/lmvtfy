package com.getbootstrap.lmvtfy.validation

sealed case class ValidationMessage(
  locationSpan: Option[SourceSpan],
  parts: Seq[MessagePart]
)
