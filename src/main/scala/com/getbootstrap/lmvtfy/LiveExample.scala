package com.getbootstrap.lmvtfy

import spray.http.Uri
import spray.http.Uri.NamedHost

sealed trait LiveExample
case class JsFiddleExample(url: Uri) extends LiveExample
case class JsBinExample(url: Uri) extends LiveExample


object JsFiddleExample {
  def unapply(uri: Uri): Option[JsFiddleExample] = {
    uri.authority.host match {
      case NamedHost("jsfiddle.net") => Some(JsFiddleExample(uri))
      case _ => None
    }
  }
}
object JsBinExample {
  def unapply(uri: Uri): Option[JsBinExample] = {
    uri.authority.host match {
      case NamedHost("jsbin.com") => Some(JsBinExample(uri))
      case _ => None
    }
  }
}
