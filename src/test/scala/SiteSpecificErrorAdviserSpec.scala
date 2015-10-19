import org.specs2.mutable._
import spray.http.Uri
import com.chrisrebert.lmvtfy.live_examples.{LiveExample, JsFiddleExample, BlOcksExample}
import com.chrisrebert.lmvtfy.validation.{Html5Validator, SiteSpecificErrorAdviser}
import com.chrisrebert.lmvtfy.validation.markdown.MarkdownValidationMessage
import com.chrisrebert.lmvtfy.util.InputSourceString

class SiteSpecificErrorAdviserSpec extends Specification {
  val dummyFiddleExample = JsFiddleExample(Uri("http://jsfiddle.net/foobar/")).get
  val dummyBlockExample = BlOcksExample(Uri("http://bl.ocks.org/mbostock/raw/1353700/")).get
  val fiddleSourceWithExtraBody =
    """
      |<!DOCTYPE html>
      |<html>
      |<head>
      |  <meta http-equiv="content-type" content="text/html; charset=UTF-8">
      |  <title> - jsFiddle demo</title>
      |  <script type='text/javascript' src='/js/lib/dummy.js'></script>
      |  <link rel="stylesheet" type="text/css" href="/css/result-light.css">
      |  <style type='text/css'>
      |  </style>
      |
      |<script type='text/javascript'>//<![CDATA[
      |window.onload=function(){
      |
      |}//]]>
      |
      |</script>
      |
      |</head>
      |<body>
      |  <body>
      |    <p>Hello</p>
      |</body>
      |
      |</body>
      |
      |</html>
    """.stripMargin
  val fiddleSourceWithExtraHtml =
    """
      |<!DOCTYPE html>
      |<html>
      |<head>
      |  <meta http-equiv="content-type" content="text/html; charset=UTF-8">
      |  <title> - jsFiddle demo</title>
      |  <script type='text/javascript' src='/js/lib/dummy.js'></script>
      |  <link rel="stylesheet" type="text/css" href="/css/result-light.css">
      |  <style type='text/css'>
      |  </style>
      |
      |<script type='text/javascript'>//<![CDATA[
      |window.onload=function(){
      |
      |}//]]>
      |
      |</script>
      |
      |</head>
      |<body>
      |  <html>
      |    <head><title>Foo</title></head>
      |<body>
      |    <p>Hello</p>
      |</body>
      |</html>
      |
      |</body>
      |
      |</html>
    """.stripMargin
  val htmlWithInvalidAttr =
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

  def extraMarkdownMessagesFor(example: LiveExample, html: String) = {
    val validationErrs = Html5Validator.validationErrorsFor(html.asInputSource).get
    SiteSpecificErrorAdviser.extraMessagesFor(example, validationErrs).map{ _.markdown }
  }

  "JSFiddle with an extra <body>" should {
    "result in a specific extra error message" in {
      extraMarkdownMessagesFor(dummyFiddleExample, fiddleSourceWithExtraBody) mustEqual Seq("JSFiddle inserts the contents of its \"HTML\" pane within ```` <body>...</body> ```` tags, so you cannot include your own ```` <body> ```` tag in your JSFiddle.")
    }
  }

  "JSFiddle with an extra <html>" should {
    "result in a specific extra error message" in {
      extraMarkdownMessagesFor(dummyFiddleExample, fiddleSourceWithExtraHtml) mustEqual Seq("JSFiddle inserts the contents of its \"HTML\" pane within ```` <body>...</body> ```` tags, so you cannot include your own ```` <html> ````, ```` <head> ````, or ```` <body> ```` tags in your JSFiddle.")
    }
  }

  "JSFiddle with a non-extra-tag-related error" should {
    "not result in an extra error message" in {
      extraMarkdownMessagesFor(dummyFiddleExample, htmlWithInvalidAttr) mustEqual Nil
    }
  }

  "Non-JSFiddle with an extra <body>" should {
    "not result in an extra error message" in {
      extraMarkdownMessagesFor(dummyBlockExample, fiddleSourceWithExtraBody) mustEqual Nil
    }
  }

  "Non-JSFiddle with an extra <html>" should {
    "not result in an extra error message" in {
      extraMarkdownMessagesFor(dummyBlockExample, fiddleSourceWithExtraHtml) mustEqual Nil
    }
  }
}
