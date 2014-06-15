package com.chrisrebert.lmvtfy

import com.chrisrebert.lmvtfy.live_examples.LiveExampleMention

sealed case class ValidationResult(markdownMessage: String, forMention: LiveExampleMention)
