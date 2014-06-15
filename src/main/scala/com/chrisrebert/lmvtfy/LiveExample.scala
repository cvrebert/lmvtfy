package com.chrisrebert.lmvtfy

import spray.http.Uri
import spray.http.Uri.{Path, NamedHost}

sealed trait LiveExample {
  def url: Uri
}
class JsFiddleExample private(val url: Uri) extends LiveExample {
  override def toString = s"JsFiddleExample(${url}})"
  override def hashCode = url.hashCode
  override def equals(other: Any) = other.isInstanceOf[JsFiddleExample] && other.asInstanceOf[JsFiddleExample].url == url
}
object JsFiddleExample {
  def apply(uri: Uri): Option[JsFiddleExample] = canonicalize(uri).map{ new JsFiddleExample(_) }
  def unapply(uri: Uri): Option[JsFiddleExample] = {
    uri.authority.host match {
      case NamedHost("jsfiddle.net") => JsFiddleExample(uri)
      case _ => None
    }
  }
  private def canonicalize(uri: Uri) = {
    val newPath = uri.path.toString.split('/') match {
      case Array("", identifier)                       => Some(Path / identifier / "show" / "")
      case Array("", identifier, "show")               => Some(Path / identifier / "show" / "")
      case Array("", identifier, "embedded", "result") => Some(Path / identifier / "show" / "")
      case Array("", identifier, revision)                       => Some(Path / identifier / revision / "show" / "")
      case Array("", identifier, revision, "show")               => Some(Path / identifier / revision / "show" / "")
      case Array("", identifier, revision, "embedded", "result") => Some(Path / identifier / revision / "show" / "")
      case _ => None
    }
    newPath.map{ uri.withPath(_) }
  }
}
class JsBinExample private(val url: Uri) extends LiveExample {
  override def toString = s"JsBinExample(${url}})"
  override def hashCode = url.hashCode
  override def equals(other: Any) = other.isInstanceOf[JsBinExample] && other.asInstanceOf[JsBinExample].url == url
}
object JsBinExample {
  def apply(uri: Uri): Option[JsBinExample] = canonicalize(uri).map{ new JsBinExample(_) }
  def unapply(uri: Uri): Option[JsBinExample] = {
    uri.authority.host match {
      case NamedHost("jsbin.com") => JsBinExample(uri)
      case _ => None
    }
  }
  private def canonicalize(uri: Uri) = {
    val newPath = uri.path.toString.split('/') match {
      case Array("", identifier)         => Some(Path / identifier)
      case Array("", identifier, "edit") => Some(Path / identifier)
      case Array("", identifier, revision)         => Some(Path / identifier / revision)
      case Array("", identifier, revision, "edit") => Some(Path / identifier / revision)
      case _ => None
    }
    newPath.map{ uri.withPath(_) }
  }
}

// FIXME: test variants with usernames