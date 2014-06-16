import org.specs2.matcher.Matcher
import org.specs2.mutable._
import spray.http.Uri
import com.chrisrebert.lmvtfy.live_examples._

class LiveExamplesExtractorSpec extends Specification {
  def assertNoLiveExamplesIn(text: String) = {
    LiveExamplesExtractor.liveExamplesFromWithin(text) must beEmpty
  }
  def assertHasLiveExample(text: String) = {
    LiveExamplesExtractor.liveExamplesFromWithin(text) must have size(1)
  }
  def onlyLiveExampleIn(text: String) = {
    val examples = LiveExamplesExtractor.liveExamplesFromWithin(text)
    examples must have size(1)
    examples.head
  }
  val haveNoQueryString: Matcher[Uri] = ((_: Uri).query.isEmpty, "")

  "LiveExamplesExtractor" should {
    "reject non-HTTP(S) URLs" in {
      assertNoLiveExamplesIn("//jsbin.com/mogupeli/3/")
      assertNoLiveExamplesIn("ftp://jsbin.com/mogupeli/3/")
      assertNoLiveExamplesIn("file://home/user/secrets")
      assertNoLiveExamplesIn("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUAAAAFCAYAAACNbyblAAAAHElEQVQI12P4//8/w38GIAXDIBKE0DHxgljNBAAO9TXL0Y4OHwAAAABJRU5ErkJggg==")
    }
    "accept HTTP URLs" in {
      assertHasLiveExample("http://jsbin.com/mogupeli/3/")
    }
    "accept HTTPS URLs" in {
      assertHasLiveExample("https://jsbin.com/mogupeli/3/")
    }
    "reject explicit ports" in {
      assertNoLiveExamplesIn("http://jsbin.com:666/mogupeli/3/")
    }
    "reject URLs with HTTP login info" in {
      assertNoLiveExamplesIn("http://username:password@jsbin.com/mogupeli/3/")
    }
    "reject IP address URLs" in {
      assertNoLiveExamplesIn("http://192.168.0.1/mogupeli/3/")
    }
    "reject relative URLs" in {
      assertNoLiveExamplesIn("/mogupeli/3/")
    }
    "eliminate fragments from URLs" in {
      val example = LiveExamplesExtractor.liveExamplesFromWithin("http://jsbin.com/mogupeli/3/#frag").head
      example.url.fragment must beNone
    }
    "eliminate querystrings from URLs" in {
      val example = LiveExamplesExtractor.liveExamplesFromWithin("http://jsbin.com/mogupeli/3/?x=y&a=1").head
      example.url.query must beEmpty
    }
    "extract JS Bin examples" in {
      assertHasLiveExample("http://jsbin.com/mogupeli/3/edit")
      assertHasLiveExample("http://jsbin.com/mogupeli/3/")
    }
    "extract JS Fiddle examples" in {
      assertHasLiveExample("http://jsfiddle.net/wYc3u/5/")
      assertHasLiveExample("http://jsfiddle.net/wYc3u/5/embedded/result/")
      assertHasLiveExample("http://jsfiddle.net/wYc3u/5/show/")
    }
    "extract Bootply examples" in {
      assertHasLiveExample("http://bootply.com/yo7LnP42F7")
      assertHasLiveExample("http://www.bootply.com/yo7LnP42F7")
      assertHasLiveExample("http://s.bootply.com/render/yo7LnP42F7")
    }
    "extract multiple examples" in {
      val examples = LiveExamplesExtractor.liveExamplesFromWithin("http://jsbin.com/mogupeli/3/   http://jsfiddle.net/wYc3u/5/")
      examples mustEqual Set(
        JsBinExample(Uri("http://jsbin.com/mogupeli/3/")).get,
        JsFiddleExample(Uri("http://jsfiddle.net/wYc3u/5/")).get
      )
    }
    "extract examples within brackets" in {
      onlyLiveExampleIn("[http://jsbin.com/mogupeli/3/]") mustEqual JsBinExample(Uri("http://jsbin.com/mogupeli/3/")).get
    }
    "extract examples within parentheses" in {
      onlyLiveExampleIn("(http://jsbin.com/mogupeli/3/)") mustEqual JsBinExample(Uri("http://jsbin.com/mogupeli/3/")).get
    }
    "extract examples near commas" in {
      onlyLiveExampleIn(",http://jsbin.com/mogupeli/3/,") mustEqual JsBinExample(Uri("http://jsbin.com/mogupeli/3/")).get
    }
  }
}
