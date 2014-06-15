import com.chrisrebert.lmvtfy.live_examples.{JsBinExample, JsFiddleExample}
import org.specs2.mutable._
import spray.http.Uri
import com.chrisrebert.lmvtfy.JsBinExample

class LiveExampleCanonicalizationSpec extends Specification {
  "JsBinExample" should {
    def bin(url: String) = JsBinExample(Uri(url)).map{ _.url }

    "canonicalize URLs correctly" in {
      // JS Bin hates trailing slashes
      val canonicalVersioned = Some(Uri("http://jsbin.com/mogupeli/3"))
      bin("http://jsbin.com/mogupeli/3") mustEqual canonicalVersioned
      bin("http://jsbin.com/mogupeli/3/") mustEqual canonicalVersioned
      bin("http://jsbin.com/mogupeli/3/edit") mustEqual canonicalVersioned
      bin("http://jsbin.com/mogupeli/3/edit/") mustEqual canonicalVersioned

      val canonicalUnversioned = Some(Uri("http://jsbin.com/mogupeli"))
      bin("http://jsbin.com/mogupeli") mustEqual canonicalUnversioned
      bin("http://jsbin.com/mogupeli/") mustEqual canonicalUnversioned
      bin("http://jsbin.com/mogupeli/edit") mustEqual canonicalUnversioned
      bin("http://jsbin.com/mogupeli/edit/") mustEqual canonicalUnversioned
    }
  }
  "JsFiddleExample" should {
    def fiddle(url: String) = JsFiddleExample(Uri(url)).map{ _.url }

    "canonicalize URLs correctly" in {
      // JS Fiddle likes trailing slashes
      val canonicalVersioned = Some(Uri("http://jsfiddle.net/wYc3u/5/show/"))
      fiddle("http://jsfiddle.net/wYc3u/5") mustEqual canonicalVersioned
      fiddle("http://jsfiddle.net/wYc3u/5/") mustEqual canonicalVersioned
      fiddle("http://jsfiddle.net/wYc3u/5/show") mustEqual canonicalVersioned
      fiddle("http://jsfiddle.net/wYc3u/5/show/") mustEqual canonicalVersioned
      fiddle("http://jsfiddle.net/wYc3u/5/embedded/result") mustEqual canonicalVersioned
      fiddle("http://jsfiddle.net/wYc3u/5/embedded/result/") mustEqual canonicalVersioned

      val canonicalUnversioned = Some(Uri("http://jsfiddle.net/wYc3u/show/"))
      fiddle("http://jsfiddle.net/wYc3u") mustEqual canonicalUnversioned
      fiddle("http://jsfiddle.net/wYc3u/") mustEqual canonicalUnversioned
      fiddle("http://jsfiddle.net/wYc3u/show") mustEqual canonicalUnversioned
      fiddle("http://jsfiddle.net/wYc3u/show/") mustEqual canonicalUnversioned
      fiddle("http://jsfiddle.net/wYc3u/embedded/result") mustEqual canonicalUnversioned
      fiddle("http://jsfiddle.net/wYc3u/embedded/result/") mustEqual canonicalUnversioned
    }
  }
}
