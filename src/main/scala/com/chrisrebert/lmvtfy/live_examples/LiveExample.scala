package com.chrisrebert.lmvtfy.live_examples

import scala.util.Try
import spray.http.Uri
import spray.http.Uri.{Authority, NamedHost, Path, Query}
import com.chrisrebert.lmvtfy.util.{HtmlSuffixed, RichUri}

sealed trait ExampleKind
case object CompleteRawHtml extends ExampleKind
case object CompleteRawHtmlMaybe extends ExampleKind
case object RawHtmlFragment extends ExampleKind
case object HtmlWithinJavaScriptWithinHtml extends ExampleKind

object LiveExample {
  def apply(url: Uri): Option[LiveExample] = {
    url match {
      case JsFiddleExample(fiddle) => Some(fiddle)
      case JsBinExample(bin) => Some(bin)
      case BootplyExample(ply) => Some(ply)
      case PlunkerExample(plunk) => Some(plunk)
      case CodePenExample(pen) => Some(pen)
      case GistExample(gist) => Some(gist)
      case BlOcksExample(block) => Some(block)
      case PastebinExample(paste) => Some(paste)
      case _ => None
    }
  }
}
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
  private val CanonicalHost = NamedHost("fiddle.jshell.net")
  private object Revision {
    def unapply(intStr: String): Option[String] = Try{ intStr.toInt }.toOption.map{ _ => intStr }
  }
  def apply(uri: Uri): Option[JsFiddleExample] = canonicalize(uri).map{ new JsFiddleExample(_) }
  def unapply(uri: Uri): Option[JsFiddleExample] = {
    uri.authority.host match {
      case NamedHost("jsfiddle.net") | CanonicalHost => JsFiddleExample(uri)
      case _ => None
    }
  }
  private def canonicalize(uri: Uri) = {
    val newPath = uri.path.toString.split('/') match {
      case Array("", identifier)                       => Some(Path / identifier / "show" / "light" / "")
      case Array("", identifier, "show")               => Some(Path / identifier / "show" / "light" / "")
      case Array("", identifier, "embedded", "result") => Some(Path / identifier / "show" / "light" / "")
      case Array("", identifier, Revision(revision))                       => Some(Path / identifier / revision / "show" / "light" / "")
      case Array("", identifier, Revision(revision), "show")               => Some(Path / identifier / revision / "show" / "light" / "")
      case Array("", identifier, Revision(revision), "embedded", "result") => Some(Path / identifier / revision / "show" / "light" / "")

      case Array("", username, identifier)                       => Some(Path / username / identifier / "show" / "light" / "")
      case Array("", username, identifier, "show")               => Some(Path / username / identifier / "show" / "light" / "")
      case Array("", username, identifier, "embedded", "result") => Some(Path / username / identifier / "show" / "light" / "")

      case Array("", username, identifier, Revision(revision))                       => Some(Path / username / identifier / revision / "show" / "light" / "")
      case Array("", username, identifier, Revision(revision), "show")               => Some(Path / username / identifier / revision / "show" / "light" / "")
      case Array("", username, identifier, Revision(revision), "embedded", "result") => Some(Path / username / identifier / revision / "show" / "light" / "")
      case _ => None
    }
    newPath.map{ uri.withPath(_).withHost(CanonicalHost).withoutQuery.withoutFragment }
  }
}

class JsBinExample private(val codeUrl: Uri) extends LiveExample {
  override val kind = HtmlWithinJavaScriptWithinHtml
  override def displayUrl = codeUrl
  override def toString = s"JsBinExample(${codeUrl})"
  override def hashCode = codeUrl.hashCode
  override def equals(other: Any) = other.isInstanceOf[JsBinExample] && other.asInstanceOf[JsBinExample].codeUrl == codeUrl
}
object JsBinExample {
  private val CanonicalHost = NamedHost("jsbin.com")
  def apply(uri: Uri): Option[JsBinExample] = canonicalize(uri).map{ new JsBinExample(_) }
  def unapply(uri: Uri): Option[JsBinExample] = JsBinExample(uri)
  private def canonicalize(uri: Uri) = {
    canonicalizedHost(uri.authority.host).flatMap{ newHost =>
      canonicalizedPath(uri.path).map{ newPath =>
        uri.withHost(newHost).withPath(newPath).withoutQuery.withoutFragment
      }
    }
  }
  private def canonicalizedHost(host: Uri.Host) = {
    host match {
      case NamedHost("output.jsbin.com") | CanonicalHost => Some(CanonicalHost)
      case _ => None
    }
  }
  private def canonicalizedPath(path: Uri.Path) = {
    path.toString.split('/') match {
      case Array("", identifier)         => Some(Path / identifier / "edit")
      case Array("", identifier, "edit") => Some(Path / identifier / "edit")
      case Array("", identifier, revision)         => Some(Path / identifier / revision / "edit")
      case Array("", identifier, revision, "edit") => Some(Path / identifier / revision / "edit")
      case _ => None
    }
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
      canonicalizedPath(uri.path).map{ newPath =>
        uri.withHost(newHost).withPath(newPath).withoutQuery.withoutFragment
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
        uri.withHost(newHost).withPath(newPath).withoutQuery.withoutFragment
      }
    }
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
        uri.withHost(newHost).withPath(newPath).withoutQuery.withoutFragment
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

class GistExample private(val codeUrl: Uri) extends LiveExample {
  import GistExample.{DisplayHost,Https}

  override val kind = CompleteRawHtmlMaybe
  override lazy val displayUrl = {
    codeUrl.path.toString.split('/') match {
      case Array("", username, gistId, "raw") => {
        val displayPath = Path / username / gistId
        Uri(scheme = Https, authority = Uri.Authority(DisplayHost), path = displayPath)
      }
      case Array("", username, gistId, "raw", fileSha) => codeUrl
      case Array("", username, gistId, "raw", fileSha, fileName) => codeUrl
      case _ => throw new IllegalStateException(s"Invalid Gist code URL: ${codeUrl}")
    }
  }
  override def toString = s"GistExample(${codeUrl})"
  override def hashCode = codeUrl.hashCode
  override def equals(other: Any) = other.isInstanceOf[GistExample] && other.asInstanceOf[GistExample].codeUrl == codeUrl
}
object GistExample {
  private val CanonicalHost = NamedHost("gist.githubusercontent.com")
  private val DisplayHost = NamedHost("gist.github.com")
  private val Https = Uri.httpScheme(securedConnection = true)
  def apply(uri: Uri): Option[GistExample] = canonicalize(uri).map{ new GistExample(_) }
  def unapply(uri: Uri): Option[GistExample] = {
    uri.authority.host match {
      case DisplayHost | CanonicalHost => GistExample(uri)
      case _ => None
    }
  }
  private def canonicalize(uri: Uri) = {
    val newPath = uri.path.toString.split('/') match {
      case Array("", username, gistId)            => Some(Path / username / gistId / "raw")
      case Array("", username, gistId, "raw")     => Some(Path / username / gistId / "raw")
      // technically, the user did specify a specific revision, but getting the corresponding fileSha would involve hitting the GitHub Gist JSON API
      // plus, it's unlikely they gave us a link to the non-current revision anyway
      case Array("", username, gistId, commitSha) => Some(Path / username / gistId / "raw")
      case Array("", username, gistId, "raw", fileSha)           => Some(Path / username / gistId / "raw" / fileSha)
      case Array("", username, gistId, "raw", fileSha, fileName) => Some(Path / username / gistId / "raw" / fileSha / fileName)
      case _ => None
    }
    newPath.map{ uri.withScheme(Https).withHost(CanonicalHost).withPath(_).withoutQuery.withoutFragment }
  }
}

class BlOcksExample private(val codeUrl: Uri) extends LiveExample {
  override val kind = CompleteRawHtml
  override def displayUrl = codeUrl
  override def toString = s"BlOcksExample(${codeUrl})"
  override def hashCode = codeUrl.hashCode
  override def equals(other: Any) = other.isInstanceOf[BlOcksExample] && other.asInstanceOf[BlOcksExample].codeUrl == codeUrl
}
object BlOcksExample {
  private val Http = Uri.httpScheme(securedConnection = false)
  private val CanonicalHost = NamedHost("bl.ocks.org")
  def apply(uri: Uri): Option[BlOcksExample] = canonicalize(uri).map{ new BlOcksExample(_) }
  def unapply(uri: Uri): Option[BlOcksExample] = {
    uri.authority.host match {
      case CanonicalHost => BlOcksExample(uri)
      case _ => None
    }
  }
  private def canonicalize(uri: Uri) = {
    val newPath = uri.path.toString.split('/') match {
      case Array("", username, "raw", gistId)               => Some(Path / username / "raw" / gistId / "")
      case Array("", username, "raw", gistId, "index.html") => Some(Path / username / "raw" / gistId / "")
      case Array("", username, "raw", gistId, commitSha)               => Some(Path / username / "raw" / gistId / commitSha / "")
      case Array("", username, "raw", gistId, commitSha, "index.html") => Some(Path / username / "raw" / gistId / commitSha / "")
      case Array("", username, gistId)                      => Some(Path / username / "raw" / gistId / "")
      case Array("", username, gistId, commitSha)                      => Some(Path / username / "raw" / gistId / commitSha / "")
      case _ => None
    }
    newPath.map{ uri.withScheme(Http).withPath(_).withoutQuery.withoutFragment }
  }
}

class PastebinExample private(val id: String) extends LiveExample {
  import PastebinExample._

  override val kind = CompleteRawHtmlMaybe
  override val codeUrl = BaseUrl.withPath(Path / "raw.php").withQuery(("i", id))
  override val displayUrl = BaseUrl.withPath(Path / id)
  override def toString = s"PastebinExample(${displayUrl})"
  override def hashCode = id.hashCode
  override def equals(other: Any) = other.isInstanceOf[PastebinExample] && other.asInstanceOf[PastebinExample].id == id
}
object PastebinExample {
  private val Http = Uri.httpScheme(securedConnection = false)
  private val CanonicalHost = NamedHost("pastebin.com")
  private val BaseUrl = Uri(Http, Authority(CanonicalHost), Path.Empty, Query.Empty)
  private val SafeIdentifier = "[0-9a-zA-Z_-]+".r
  def apply(uri: Uri): Option[PastebinExample] = idFrom(uri).map{ new PastebinExample(_) }
  def unapply(uri: Uri): Option[PastebinExample] = {
    uri.authority.host match {
      case CanonicalHost => PastebinExample(uri)
      case _ => None
    }
  }
  private def idFrom(uri: Uri): Option[String] = {
    (uri.path.toString.split('/') match {
      case Array("", "raw.php") => uri.query.toMap.get("i")
      case Array("", id) => Some(id)
      case _ => None
    }).filter{
      case SafeIdentifier(_*) => true
      case _ => false
    }
  }
}
