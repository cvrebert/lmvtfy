package com.chrisrebert.lmvtfy

import com.chrisrebert.lmvtfy.live_examples.LiveExampleMention

sealed case class ValidationResult(message: Markdown, forMention: LiveExampleMention)
