package com.chrisrebert.lmvtfy.validation

sealed case class ValidationMessage(
  locationSpan: Option[SourceSpan],
  parts: Seq[MessagePart]
)
