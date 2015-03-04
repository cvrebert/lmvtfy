import org.specs2.mutable._
import com.chrisrebert.lmvtfy.util.IsHtmlish

class IsHtmlishSpec extends Specification {
  "Heuristic for 'Is this string HTML?'" should {
    "not match plain text" in {
      IsHtmlish("foo the bar < baz > quux < hootananny > y'all") must beFalse
    }
    "not match plain text that just mentions an opening HTML tag" in {
      IsHtmlish("it is too late the <span> cannot hold") must beFalse
    }
    "not match plain text that just mentions a closing HTML tag" in {
      IsHtmlish("it is too late the </span> cannot stop it") must beFalse
    }
    "match a realistic HTML example" in {
      val realisticHtml =
        """<!DOCTYPE html>
          |<html lang="en">
          |  <head>
          |    <meta charset="utf-8">
          |    <title>Title</title>
          |  </head>
          |  <body></body>
          |</html>
        """.stripMargin

      IsHtmlish(realisticHtml) must beTrue
    }
    "match an ALL-CAPS HTML example" in {
      IsHtmlish("and <DIV> thus it was that Sir Robin</DIV> did run away") must beTrue
    }
    "match a lowercase HTML example" in {
      IsHtmlish("and <div> thus it was that Sir Robin</div> did run away") must beTrue
    }
  }
}
