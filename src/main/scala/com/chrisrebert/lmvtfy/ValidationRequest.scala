package com.chrisrebert.lmvtfy

import akka.util.ByteString
import com.chrisrebert.lmvtfy.live_examples.LiveExampleMention

sealed case class ValidationRequest(htmlBytes: ByteString, mention: LiveExampleMention)
