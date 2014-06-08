import org.specs2.mutable._
import com.getbootstrap.lmvtfy.validation.XhtmlRedactor

class XhtmlRedactorSpec extends Specification {
  "XHTML" should {
    "be replaced in all instances by HTML" in {
      val redacted = XhtmlRedactor.redact("XHTML not allowed as child of XHTML element XHTML")
      redacted mustEqual "HTML not allowed as child of HTML element HTML"
    }
    "be left untouched when part of another word" in {
      XhtmlRedactor.redact("XHTMLology XHTML pseudoXHTML") mustEqual "XHTMLology HTML pseudoXHTML"
    }
    "be ignored when lowercase" in {
      val orig = "xhtml can xhtml my xhtml"
      XhtmlRedactor.redact(orig) mustEqual orig
    }
  }
}
