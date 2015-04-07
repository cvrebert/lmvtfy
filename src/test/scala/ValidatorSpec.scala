import java.io.StringReader
import org.xml.sax.InputSource
import org.specs2.mutable._
import com.chrisrebert.lmvtfy.validation._

class ValidatorSpec extends Specification {
  implicit class HtmlString(str: String) {
    def inputSource: InputSource = new InputSource(new StringReader(str))
  }

  val httpEquivErrText = Vector(PlainText("Bad value "), CodeText("Gibberish"), PlainText(" for attribute "), CodeText("http-equiv"), PlainText(" on element "), CodeText("meta"), PlainText("."))
  val httpEquivErrSpan = SourceSpan(5, 56, 5, 56).get
  val httpEquivValidationMsg = ValidationMessage(Some(httpEquivErrSpan), httpEquivErrText)

  "Bad meta http-equiv" should {
    val badHtml =
      """<!DOCTYPE html>
        |<html lang="en">
        |  <head>
        |    <meta charset="utf-8">
        |    <meta http-equiv="Gibberish" content="gobbledegook">
        |    <title>Title</title>
        |  </head>
        |  <body></body>
        |</html>
      """.stripMargin

    "cause a validation error" in {
      val messages = Html5Validator.validationErrorsFor(badHtml.inputSource).get
      messages must have size(1)
      messages.head mustEqual httpEquivValidationMsg
    }
  }

  "Bad meta http-equiv and non-table-related child of a table" should {
    val badHtml =
      """<!DOCTYPE html>
        |<html lang="en">
        |  <head>
        |    <meta charset="utf-8">
        |    <meta http-equiv="Gibberish" content="gobbledegook">
        |    <title>Title</title>
        |  </head>
        |  <body>
        |    <ul>
        |      <p>Can't just randomly put a paragraph tag here!</p>
        |    </ul>
        |  </body>
        |</html>
      """.stripMargin

    "cause 2 validation errors" in {
      val pInUlMsg = ValidationMessage(SourceSpan(10, 9, -1, -1), Vector(PlainText("Element "), CodeText("p"), PlainText(" not allowed as child of element "), CodeText("ul"), PlainText(" in this context. (Suppressing further errors from this subtree.)")))

      val messages = Html5Validator.validationErrorsFor(badHtml.inputSource).get
      messages must have size(2)
      messages(0) mustEqual httpEquivValidationMsg
      messages(1) mustEqual pInUlMsg
    }
  }

  "Incorrect attribute" should {
    val badHtml =
      """<!DOCTYPE html>
        |<html lang="en">
        |  <head>
        |    <meta charset="utf-8">
        |    <title>Title</title>
        |  </head>
        |  <body>
        |    <span href="http://bad.example">Hello</span>
        |  </body>
        |</html>
      """.stripMargin

    "result in a properly-spelled validation error message" in {
      val expectedMsg = ValidationMessage(SourceSpan(8, 36, -1, -1), Vector(PlainText("Attribute "), CodeText("href"), PlainText(" not allowed on element "), CodeText("span"), PlainText(" at this point.")))

      val messages = Html5Validator.validationErrorsFor(badHtml.inputSource).get
      messages must have size(1)
      messages.head mustEqual expectedMsg
    }
  }

  "X-UA-Compatible meta tag" should {
    val badHtml =
      """<!DOCTYPE html>
        |<html lang="en">
        |  <head>
        |    <meta charset="utf-8">
        |    <meta http-equiv="X-UA-Compatible" content="IE=edge">
        |    <meta name="viewport" content="width=device-width, initial-scale=1">
        |    <meta name="description" content="">
        |    <meta name="author" content="">
        |    <link rel="shortcut icon" href="favicon.ico">
        |    <title>Title</title>
        |  </head>
        |  <body>
        |    <p>Hello</p>
        |  </body>
        |</html>
      """.stripMargin

    "not be considered a validation error" in {
      val messages = Html5Validator.validationErrorsFor(badHtml.inputSource).get
      messages must have size(0)
    }
  }

  "img tag missing an alt attribute" should {
    val mehHtml =
      """<!DOCTYPE html>
        |<html lang="en">
        |  <head>
        |    <meta charset="utf-8">
        |    <title>Title</title>
        |  </head>
        |  <body>
        |    <img src="/foobar.jpg" />
        |  </body>
        |</html>
      """.stripMargin

    "not be considered a validation error" in {
      val messages = Html5Validator.validationErrorsFor(mehHtml.inputSource).get
      messages must have size(0)
    }
  }

  "input[type=\"date\"]" should {
    val goodHtml =
      """<!DOCTYPE html>
        |<html lang="en">
        |  <head>
        |    <meta charset="utf-8">
        |    <title>Title</title>
        |  </head>
        |  <body>
        |    <input type="date" />
        |  </body>
        |</html>
      """.stripMargin

    "not cause any validation messages" in {
      val messages = Html5Validator.validationErrorsFor(goodHtml.inputSource).get
      messages must have size(0)
    }
  }

  "missing title tag" should {
    val mehHtml =
      """<!DOCTYPE html>
        |<html lang="en">
        |  <head>
        |    <meta charset="utf-8">
        |  </head>
        |  <body>
        |  </body>
        |</html>
      """.stripMargin

    "not be considered a validation error" in {
      val messages = Html5Validator.validationErrorsFor(mehHtml.inputSource).get
      messages must have size(0)
    }
  }

  "data-* attributes on SVG elements" should {
    val mehHtml =
      """<!DOCTYPE html>
        |<html lang="en">
        |  <head>
        |    <meta charset="utf-8">
        |    <title>Title</title>
        |  </head>
        |  <body>
        |    <svg>
        |      <rect x="0" y="0" width="100" height="100" data-foo="bar" />
        |    </svg>
        |  </body>
        |</html>
      """.stripMargin
    //
    "not be considered a validation error" in {
      val messages = Html5Validator.validationErrorsFor(mehHtml.inputSource).get
      messages must have size(0)
    }
  }

  "JS Fiddle's weird meta tag" should {
    val fiddlyHtml =
      """<!DOCTYPE html>
        |<html lang="en">
        |  <head>
        |    <meta charset="utf-8">
        |    <meta http-equiv="edit-Type" edit="text/html; charset=utf-8" />
        |    <title>Title</title>
        |  </head>
        |  <body></body>
        |</html>
      """.stripMargin

    "not be considered a validation error" in {
      val messages = Html5Validator.validationErrorsFor(fiddlyHtml.inputSource).get
      messages must have size(0)
    }
  }

  "autocomplete attribute" should {
    "not be considered a validation error on an input" in {
      val autoInputHtml =
        """<!DOCTYPE html>
          |<html lang="en">
          |  <head>
          |    <meta charset="utf-8">
          |    <title>Title</title>
          |  </head>
          |  <body>
          |    <input type="radio" autocomplete="off" name="telegraph" value="SOS" />
          |  </body>
          |</html>
        """.stripMargin
      val messages = Html5Validator.validationErrorsFor(autoInputHtml.inputSource).get
      messages must have size(0)
    }
    "not be considered a validation error on a button" in {
      val autoButtonHtml =
        """<!DOCTYPE html>
          |<html lang="en">
          |  <head>
          |    <meta charset="utf-8">
          |    <title>Title</title>
          |  </head>
          |  <body>
          |    <button type="button" autocomplete="off">Click me</button>
          |  </body>
          |</html>
        """.stripMargin
      val messages = Html5Validator.validationErrorsFor(autoButtonHtml.inputSource).get
      messages must have size(0)
    }
  }

  "area tag missing an alt attribute" should {
    "not be considered a validation error" in {
      val areaWithoutAltHtml =
        """<!DOCTYPE html>
          |<html lang="en">
          |  <head>
          |    <meta charset="utf-8">
          |    <title>Title</title>
          |  </head>
          |  <body>
          |    <map name="not-the-territory">
          |      <area id="abstraction" shape="circle" coords="90,90,50" href="#" />
          |    </map>
          |  </body>
          |</html>
        """.stripMargin
      val messages = Html5Validator.validationErrorsFor(areaWithoutAltHtml.inputSource).get
      messages must have size(0)
    }
  }
}
