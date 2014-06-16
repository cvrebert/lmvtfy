package com.chrisrebert.lmvtfy

import java.nio.charset.Charset
import scala.collection.mutable
import scala.util.Try

package object util {
  private val utf8 = Charset.forName("UTF-8")

  implicit class Utf8String(str: String) {
    def utf8Bytes: Array[Byte] = str.getBytes(utf8)
  }

  implicit class Utf8ByteArray(bytes: Array[Byte]) {
    def utf8String: Try[String] = Try { new String(bytes, utf8) }
  }

  implicit class RichStack[T](stack: mutable.Stack[T]) {
    def popOption(): Option[T] = Try{ stack.pop() }.toOption
    def topOption: Option[T] = Try{ stack.top }.toOption
  }
}
