import org.specs2.mutable._
import spray.http.Uri
import com.chrisrebert.lmvtfy.live_examples._

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

    "canonicalize URLs for anonymous users correctly" in {
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

    "canonicalize URLs for logged-in users correctly" in {
      val canonicalUnversioned = Some(Uri("http://jsfiddle.net/cvrebert/7aKxf/show/"))
      fiddle("http://jsfiddle.net/cvrebert/7aKxf") mustEqual canonicalUnversioned
      fiddle("http://jsfiddle.net/cvrebert/7aKxf/") mustEqual canonicalUnversioned
      fiddle("http://jsfiddle.net/cvrebert/7aKxf/show") mustEqual canonicalUnversioned
      fiddle("http://jsfiddle.net/cvrebert/7aKxf/show/") mustEqual canonicalUnversioned
      fiddle("http://jsfiddle.net/cvrebert/7aKxf/embedded/result") mustEqual canonicalUnversioned
      fiddle("http://jsfiddle.net/cvrebert/7aKxf/embedded/result/") mustEqual canonicalUnversioned

      val canonicalVersioned = Some(Uri("http://jsfiddle.net/cvrebert/7aKxf/1/show/"))
      fiddle("http://jsfiddle.net/cvrebert/7aKxf/1") mustEqual canonicalVersioned
      fiddle("http://jsfiddle.net/cvrebert/7aKxf/1/") mustEqual canonicalVersioned
      fiddle("http://jsfiddle.net/cvrebert/7aKxf/1/show") mustEqual canonicalVersioned
      fiddle("http://jsfiddle.net/cvrebert/7aKxf/1/show/") mustEqual canonicalVersioned
      fiddle("http://jsfiddle.net/cvrebert/7aKxf/1/embedded/result") mustEqual canonicalVersioned
      fiddle("http://jsfiddle.net/cvrebert/7aKxf/1/embedded/result/") mustEqual canonicalVersioned
    }
  }

  "BootplyExample" should {
    def ply(url: String) = BootplyExample(Uri(url)).map{ _.url }

    "canonicalize URLs correctly" in {
      // Bootply hates trailing slashes, has no versioning, and logged-in users don't get different URLs
      val canonical = Some(Uri("http://s.bootply.com/render/yo7LnP42F7")) // NOTE: not normally user-facing

      ply("http://bootply.com/yo7LnP42F7") mustEqual canonical
      ply("http://bootply.com/yo7LnP42F7/") mustEqual canonical
      ply("http://www.bootply.com/yo7LnP42F7")  mustEqual canonical // this one is canonical for user-facing
      ply("http://www.bootply.com/yo7LnP42F7/") mustEqual canonical
      ply("http://s.bootply.com/render/yo7LnP42F7") mustEqual canonical
    }
  }

  "PlunkerExample" should {
    def plunk(url: String) = PlunkerExample(Uri(url)).map{ _.url }

    "canonicalize URLs correctly" in {
      // Plunker has no in-URL versioning, and logged-in users don't get different URLs
      val canonical = Some(Uri("http://run.plnkr.co/plunks/XRNnDt/"))

      plunk("http://plnkr.co/edit/XRNnDt") mustEqual canonical
      plunk("http://plnkr.co/edit/XRNnDt/") mustEqual canonical
      plunk("http://plnkr.co/edit/XRNnDt?p=preview") mustEqual canonical
      plunk("http://plnkr.co/edit/XRNnDt?p=info") mustEqual canonical
      plunk("http://plnkr.co/edit/XRNnDt/?p=info") mustEqual canonical
      plunk("http://embed.plnkr.co/XRNnDt/preview") mustEqual canonical
      plunk("http://embed.plnkr.co/XRNnDt/preview/") mustEqual canonical
      plunk("http://run.plnkr.co/plunks/XRNnDt") mustEqual canonical
      plunk("http://run.plnkr.co/plunks/XRNnDt/") mustEqual canonical
    }
  }

  "CodePenExample" should {
    // Doesn't appear to support versioning
    // URLs for anonymous users follow the same scheme, with the username being "anon"

    def pen(url: String) = CodePenExample(Uri(url)).map{ _.url }

    "canonicalize URLs for logged-in users correctly" in {
      val canonical = Some(Uri("http://s.codepen.io/shdigitaldesign/full/KsFqH"))

      pen("http://codepen.io/shdigitaldesign/pen/KsFqH") mustEqual canonical
      pen("http://codepen.io/shdigitaldesign/pen/KsFqH/") mustEqual canonical
      pen("http://codepen.io/shdigitaldesign/details/KsFqH") mustEqual canonical
      pen("http://codepen.io/shdigitaldesign/details/KsFqH/") mustEqual canonical
      pen("http://codepen.io/shdigitaldesign/full/KsFqH") mustEqual canonical
      pen("http://codepen.io/shdigitaldesign/full/KsFqH/") mustEqual canonical
      pen("http://s.codepen.io/shdigitaldesign/full/KsFqH") mustEqual canonical
      pen("http://s.codepen.io/shdigitaldesign/full/KsFqH/") mustEqual canonical
    }
  }
}
