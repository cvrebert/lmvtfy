import org.specs2.mutable._
import com.chrisrebert.lmvtfy.live_examples._

class JsBinUserHtmlFromStartJsSpec extends Specification {
  "JS Bin user HTML from start.js extractor" should {
    "successfully and correctly extract the user-level embedded HTML from a realistic example" in {
      val expectedUserHtml =
          """<!DOCTYPE html>
            |<html>
            |  <head>
            |    <meta charset=utf-8>
            |    <title>Bootstrap Bug Report</title>
            |
            |    <!-- Bootstrap CSS -->
            |    <link rel="stylesheet" href="http://getbootstrap.com/dist/css/bootstrap.min.css">
            |  </head>
            |
            |  <body>
            |
            |
            |    <!-- Follow these instructions, please! -->
            |<div class="container">
            |    <div class="row">
            |
            |        <div class="input-group input-group-lg">
            |            <!-- Input -->
            |            <input type="text" class="form-control">
            |
            |            <!-- Dropdown -->
            |            <div class="input-group-btn">
            |                <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown">Advanced <span class="caret"></span></button>
            |                <ul class="dropdown-menu pull-right" role="menu">
            |                    <li><a href="#">Option 1</a></li>
            |                    <li><a href="#">Option 2</a></li>
            |                    <li><a href="#">Option 3</a></li>
            |                    <li class="divider"></li>
            |                    <li><a href="#">Option 4</a></li>
            |                </ul>
            |
            |                <button class="btn btn-default" type="button">Submit</button>
            |          </span>
            |
            |        </div>
            |
            |</div>
            |</div>
            |
            |
            |  </body>
            |</html>""".stripMargin
      val binJs =
          """start({"html":"<!DOCTYPE html>\n<html>\n  <head>\n    <meta charset=utf-8>\n    <title>Bootstrap Bug Report</title>\n\n    <\!-- Bootstrap CSS -->\n    <link rel=\"stylesheet\" href=\"http://getbootstrap.com/dist/css/bootstrap.min.css\">\n  </head>\n\n  <body>\n\n\n    <\!-- Follow these instructions, please! -->\n<div class=\"container\">\n    <div class=\"row\">\n\n        <div class=\"input-group input-group-lg\">\n            <\!-- Input -->\n            <input type=\"text\" class=\"form-control\">\n\n            <\!-- Dropdown -->\n            <div class=\"input-group-btn\">\n                <button type=\"button\" class=\"btn btn-default dropdown-toggle\" data-toggle=\"dropdown\">Advanced <span class=\"caret\"></span></button>\n                <ul class=\"dropdown-menu pull-right\" role=\"menu\">\n                    <li><a href=\"#\">Option 1</a></li>\n                    <li><a href=\"#\">Option 2</a></li>\n                    <li><a href=\"#\">Option 3</a></li>\n                    <li class=\"divider\"></li>\n                    <li><a href=\"#\">Option 4</a></li>\n                </ul>\n\n                <button class=\"btn btn-default\" type=\"button\">Submit</button>\n          </span>\n\n        </div>\n\n</div>\n</div>\n\n\n  </body>\n</html>","css":"","javascript":"","url":"http://jsbin.com/kegunocasoye"}, {"root":"http://jsbin.com","shareRoot":"http://jsbin.com","runner":"http://run.jsbin.com/runner","static":"http://static.jsbin.com","version":"3.19.1","state":{"token":"tok","stream":false,"streaming":false,"code":"kegunocasoye","revision":1,"processors":{"html":"html","css":"css","javascript":"javascript"},"checksum":null,"metadata":{"name":"anonymous","visibility":"public","last_updated":"2014-09-18T15:23:27.000Z"}},"settings":{"panels":["html","live"]},"user":{"name":"cvrebert","key":"$2","email":"jsbin@jsbin.jsbin","last_login":"2014-09-30T20:57:46.000Z","created":"2014-06-15T22:20:12.000Z","updated":"2014-09-04T18:42:13.000Z","api_key":null,"github_token":null,"github_id":null,"verified":0,"pro":0,"id":70675,"settings":{"panels":["html","live"],"editor":{},"font":14,"addons":{"closebrackets":true,"highlight":false,"vim":false,"emacs":false,"trailingspace":false,"fold":false,"sublime":false,"tern":false,"activeline":true,"matchbrackets":false},"includejs":true,"gui":{"toppanel":false}},"dropbox_token":null,"dropbox_id":null,"beta":null,"flagged":null,"last_seen":"2014-10-07T22:29:14.000Z","lastLogin":"2014-09-30T20:57:46.000Z","avatar":"//www.gravatar.com/avatar","bincount":46}}, this, document);
            |""".stripMargin
      val userHtmlOption = binJs match {
        case JsBinUserHtmlFromStartJs(binUserHtml) => Some(binUserHtml)
        case _ => None
      }

      userHtmlOption must beSome(expectedUserHtml)
    }
  }
}
