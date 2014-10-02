import org.specs2.mutable._
import com.chrisrebert.lmvtfy.server.HtmlFragment
import com.chrisrebert.lmvtfy.util._

class HtmlFragmentSpec extends Specification {
  "HTML fragments" should {
    "be correctly converted to full documents" in {
      val fragment = "<p>Foo bar</p>"
      val whole =
        """<!DOCTYPE html>
          |<html>
          |<head>
          |    <meta charset="utf-8" />
          |    <title>Untitled</title>
          |</head>
          |<body>
          |<p>Foo bar</p>
          |</body>
          |</html>""".stripMargin
      val defragmented = HtmlFragment(fragment.utf8ByteString).asCompleteHtmlDoc.utf8String
      defragmented mustEqual whole
    }
  }
}
