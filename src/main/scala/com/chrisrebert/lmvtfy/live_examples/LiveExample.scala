package com.chrisrebert.lmvtfy.live_examples

import scala.util.Try
import spray.http.Uri
import spray.http.Uri.{Path, NamedHost}
import com.chrisrebert.lmvtfy.util.{HtmlSuffixed, RichUri}

sealed trait ExampleKind
case object CompleteRawHtml extends ExampleKind
case object RawHtmlFragment extends ExampleKind
case object JsonContainingHtml extends ExampleKind

sealed trait LiveExample {
  def displayUrl: Uri
  def codeUrl: Uri
  val kind: ExampleKind
}
class JsFiddleExample private(val codeUrl: Uri) extends LiveExample {
  override val kind = CompleteRawHtml
  override def displayUrl = codeUrl
  override def toString = s"JsFiddleExample(${codeUrl})"
  override def hashCode = codeUrl.hashCode
  override def equals(other: Any) = other.isInstanceOf[JsFiddleExample] && other.asInstanceOf[JsFiddleExample].codeUrl == codeUrl
}
object JsFiddleExample {
  private object Revision {
    def unapply(intStr: String): Option[String] = Try{ intStr.toInt }.toOption.map{ _ => intStr }
  }
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
      case Array("", identifier, Revision(revision))                       => Some(Path / identifier / revision / "show" / "")
      case Array("", identifier, Revision(revision), "show")               => Some(Path / identifier / revision / "show" / "")
      case Array("", identifier, Revision(revision), "embedded", "result") => Some(Path / identifier / revision / "show" / "")

      case Array("", username, identifier)                       => Some(Path / username / identifier / "show" / "")
      case Array("", username, identifier, "show")               => Some(Path / username / identifier / "show" / "")
      case Array("", username, identifier, "embedded", "result") => Some(Path / username / identifier / "show" / "")

      case Array("", username, identifier, Revision(revision))                       => Some(Path / username / identifier / revision / "show" / "")
      case Array("", username, identifier, Revision(revision), "show")               => Some(Path / username / identifier / revision / "show" / "")
      case Array("", username, identifier, Revision(revision), "embedded", "result") => Some(Path / username / identifier / revision / "show" / "")
      case _ => None
    }
    newPath.map{ uri.withPath(_) }
  }
}

class JsBinExample private(val codeUrl: Uri) extends LiveExample {
  override val kind = JsonContainingHtml
  override def toString = s"JsBinExample(${codeUrl})"
  override def hashCode = codeUrl.hashCode
  override def equals(other: Any) = other.isInstanceOf[JsBinExample] && other.asInstanceOf[JsBinExample].codeUrl == codeUrl
  override def displayUrl = {
    val newPath = codeUrl.path.toString.split('/') match {
      case Array("", "api", identifier, revision) => Path / identifier / revision
      case Array("", "api", identifier) => Path / identifier
      case _ => codeUrl.path
    }
    codeUrl.withPath(newPath)
  }
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
      case Array("", identifier)         => Some(Path / "api" / identifier)
      case Array("", identifier, "edit") => Some(Path / "api" / identifier)
      case Array("", identifier, revision)         => Some(Path / "api" / identifier / revision)
      case Array("", identifier, revision, "edit") => Some(Path / "api" / identifier / revision)
      case _ => None
    }
    newPath.map{ uri.withPath(_) }
  }
}

class BootplyExample private(val codeUrl: Uri) extends LiveExample {
  override val kind = CompleteRawHtml
  override def displayUrl = codeUrl
  override def toString = s"BootplyExample(${codeUrl})"
  override def hashCode = codeUrl.hashCode
  override def equals(other: Any) = other.isInstanceOf[BootplyExample] && other.asInstanceOf[BootplyExample].codeUrl == codeUrl
}
object BootplyExample {
  private val CanonicalHost = NamedHost("s.bootply.com")
  def apply(uri: Uri): Option[BootplyExample] = canonicalize(uri).map{ new BootplyExample(_) }
  def unapply(uri: Uri): Option[BootplyExample] = BootplyExample(uri)
  private def canonicalize(uri: Uri) = {
    canonicalizedHost(uri.authority.host).flatMap{ newHost =>
      canonicalizedPath(uri.path).map { newPath =>
        uri.withHost(newHost).withPath(newPath)
      }
    }
  }
  private def canonicalizedHost(host: Uri.Host) = {
    host match {
      case NamedHost("bootply.com") | NamedHost("www.bootply.com") | CanonicalHost => Some(CanonicalHost)
      case _ => None
    }
  }
  private def canonicalizedPath(path: Uri.Path) = {
    val maybeIdentifier = path.toString.split('/') match {
      case Array("", "render", identifier) => Some(identifier)
      case Array("", identifier)           => Some(identifier)
      case _ => None
    }
    maybeIdentifier.map{ Path / "render" / _ }
  }
}

class PlunkerExample private(val codeUrl: Uri) extends LiveExample {
  override val kind = CompleteRawHtml
  override def displayUrl = codeUrl
  override def toString = s"PlunkerExample(${codeUrl}})"
  override def hashCode = codeUrl.hashCode
  override def equals(other: Any) = other.isInstanceOf[PlunkerExample] && other.asInstanceOf[PlunkerExample].codeUrl == codeUrl
}
object PlunkerExample {
  private val CanonicalHost = NamedHost("run.plnkr.co")
  def apply(uri: Uri): Option[PlunkerExample] = canonicalize(uri).map{ new PlunkerExample(_) }
  def unapply(uri: Uri): Option[PlunkerExample] = PlunkerExample(uri)
  private def canonicalize(uri: Uri) = {
    canonicalizedHost(uri.authority.host).flatMap{ newHost =>
      canonicalizedPath(uri.path).map { newPath =>
        uri.withHost(newHost).withPath(newPath)
      }
    }.map{ _.withoutQuery }
  }
  private def canonicalizedHost(host: Uri.Host) = {
    host match {
      case NamedHost("plnkr.co") | NamedHost("embed.plnkr.co") | CanonicalHost => Some(CanonicalHost)
      case _ => None
    }
  }
  private def canonicalizedPath(path: Uri.Path) = {
    val maybeIdentifier = path.toString.split('/') match {
      case Array("", "edit", identifier) => Some(identifier)
      case Array("", "plunks", identifier) => Some(identifier)
      case Array("", identifier, "preview") => Some(identifier)
      case _ => None
    }
    maybeIdentifier.map{ Path / "plunks" / _ / "" }
  }
}

class CodePenExample private(val codeUrl: Uri) extends LiveExample {
  override val kind = RawHtmlFragment
  override def displayUrl = codeUrl
  override def toString = s"CodePenExample(${codeUrl}})"
  override def hashCode = codeUrl.hashCode
  override def equals(other: Any) = other.isInstanceOf[CodePenExample] && other.asInstanceOf[CodePenExample].codeUrl == codeUrl
}
object CodePenExample {
  private val CanonicalHost = NamedHost("codepen.io")
  def apply(uri: Uri): Option[CodePenExample] = canonicalize(uri).map{ new CodePenExample(_) }
  def unapply(uri: Uri): Option[CodePenExample] = CodePenExample(uri)
  private def canonicalize(uri: Uri) = {
    canonicalizedHost(uri.authority.host).flatMap{ newHost =>
      canonicalizedPath(uri.path).map { newPath =>
        uri.withHost(newHost).withPath(newPath)
      }
    }
  }
  private def canonicalizedHost(host: Uri.Host) = {
    host match {
      case CanonicalHost | NamedHost("s.codepen.io") => Some(CanonicalHost)
      case _ => None
    }
  }
  private def canonicalizedPath(path: Uri.Path) = {
    path.toString.split('/') match {
      case Array("", username, view, HtmlSuffixed(identifier)) => Some(Path / username / "pen" / identifier)
      case _ => None
    }
  }
}
