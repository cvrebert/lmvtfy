package com.chrisrebert.lmvtfy

import java.nio.charset.Charset
import scala.util.Try

package object util {
  private val utf8 = Charset.forName("UTF-8")

  implicit class Utf8String(str: String) {
    def utf8Bytes: Array[Byte] = Try{ str.getBytes(utf8) }.getOrElse{ new Array[Byte](0) }
  }
}
