import org.specs2.mutable._
import spray.http.Uri
import com.chrisrebert.lmvtfy.live_examples._

class LiveExampleCanonicalizationSpec extends Specification {
  "JsBinExample" should {
    def bin(url: String) = JsBinExample(Uri(url)).map{ _.codeUrl }

    "canonicalize URLs correctly" in {
      // JS Bin hates trailing slashes
      val canonicalVersioned = Some(Uri("http://jsbin.com/mogupeli/3/edit"))
      bin("http://jsbin.com/mogupeli/3") mustEqual canonicalVersioned
      bin("http://jsbin.com/mogupeli/3/") mustEqual canonicalVersioned
      bin("http://jsbin.com/mogupeli/3/edit") mustEqual canonicalVersioned
      bin("http://jsbin.com/mogupeli/3/edit/") mustEqual canonicalVersioned
      bin("http://output.jsbin.com/mogupeli/3") mustEqual canonicalVersioned

      val canonicalUnversioned = Some(Uri("http://jsbin.com/mogupeli/edit"))
      bin("http://jsbin.com/mogupeli") mustEqual canonicalUnversioned
      bin("http://jsbin.com/mogupeli/") mustEqual canonicalUnversioned
      bin("http://jsbin.com/mogupeli/edit") mustEqual canonicalUnversioned
      bin("http://jsbin.com/mogupeli/edit/") mustEqual canonicalUnversioned
      bin("http://output.jsbin.com/mogupeli") mustEqual canonicalUnversioned
    }
  }

  "JsFiddleExample" should {
    def fiddle(url: String) = JsFiddleExample(Uri(url)).map{ _.codeUrl }

    "canonicalize URLs for anonymous users correctly" in {
      // JS Fiddle likes trailing slashes
      val canonicalVersioned = Some(Uri("http://fiddle.jshell.net/wYc3u/5/show/light/"))
      fiddle("http://jsfiddle.net/wYc3u/5") mustEqual canonicalVersioned
      fiddle("http://jsfiddle.net/wYc3u/5/") mustEqual canonicalVersioned
      fiddle("http://jsfiddle.net/wYc3u/5/show") mustEqual canonicalVersioned
      fiddle("http://jsfiddle.net/wYc3u/5/show/") mustEqual canonicalVersioned
      fiddle("http://jsfiddle.net/wYc3u/5/embedded/result") mustEqual canonicalVersioned
      fiddle("http://jsfiddle.net/wYc3u/5/embedded/result/") mustEqual canonicalVersioned

      val canonicalUnversioned = Some(Uri("http://fiddle.jshell.net/wYc3u/show/light/"))
      fiddle("http://jsfiddle.net/wYc3u") mustEqual canonicalUnversioned
      fiddle("http://jsfiddle.net/wYc3u/") mustEqual canonicalUnversioned
      fiddle("http://jsfiddle.net/wYc3u/show") mustEqual canonicalUnversioned
      fiddle("http://jsfiddle.net/wYc3u/show/") mustEqual canonicalUnversioned
      fiddle("http://jsfiddle.net/wYc3u/embedded/result") mustEqual canonicalUnversioned
      fiddle("http://jsfiddle.net/wYc3u/embedded/result/") mustEqual canonicalUnversioned
    }

    "canonicalize URLs for logged-in users correctly" in {
      val canonicalUnversioned = Some(Uri("http://fiddle.jshell.net/cvrebert/7aKxf/show/light/"))
      fiddle("http://jsfiddle.net/cvrebert/7aKxf") mustEqual canonicalUnversioned
      fiddle("http://jsfiddle.net/cvrebert/7aKxf/") mustEqual canonicalUnversioned
      fiddle("http://jsfiddle.net/cvrebert/7aKxf/show") mustEqual canonicalUnversioned
      fiddle("http://jsfiddle.net/cvrebert/7aKxf/show/") mustEqual canonicalUnversioned
      fiddle("http://jsfiddle.net/cvrebert/7aKxf/embedded/result") mustEqual canonicalUnversioned
      fiddle("http://jsfiddle.net/cvrebert/7aKxf/embedded/result/") mustEqual canonicalUnversioned

      val canonicalVersioned = Some(Uri("http://fiddle.jshell.net/cvrebert/7aKxf/1/show/light/"))
      fiddle("http://jsfiddle.net/cvrebert/7aKxf/1") mustEqual canonicalVersioned
      fiddle("http://jsfiddle.net/cvrebert/7aKxf/1/") mustEqual canonicalVersioned
      fiddle("http://jsfiddle.net/cvrebert/7aKxf/1/show") mustEqual canonicalVersioned
      fiddle("http://jsfiddle.net/cvrebert/7aKxf/1/show/") mustEqual canonicalVersioned
      fiddle("http://jsfiddle.net/cvrebert/7aKxf/1/embedded/result") mustEqual canonicalVersioned
      fiddle("http://jsfiddle.net/cvrebert/7aKxf/1/embedded/result/") mustEqual canonicalVersioned
    }
  }

  "BootplyExample" should {
    def ply(url: String) = BootplyExample(Uri(url)).map{ _.codeUrl }

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
    def plunk(url: String) = PlunkerExample(Uri(url)).map{ _.codeUrl }

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

    def pen(url: String) = CodePenExample(Uri(url)).map{ _.codeUrl }

    "canonicalize URLs for logged-in users correctly" in {
      val canonical = Some(Uri("http://codepen.io/shdigitaldesign/pen/KsFqH.html"))

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

  "GistExample" should {
    def codeGist(url: String) = GistExample(Uri(url)).map{ _.codeUrl }
    def dispGist(url: String) = GistExample(Uri(url)).map{ _.displayUrl }

    val canonicalVersionedCode = Some(Uri("https://gist.githubusercontent.com/anonymous/de6e64bd8b3b01eefa2f/raw/487e0f2eb3d3d39d3f4555e1407089845943579c"))
    val canonicalVersionedCodeWithFilename = Some(Uri("https://gist.githubusercontent.com/anonymous/de6e64bd8b3b01eefa2f/raw/487e0f2eb3d3d39d3f4555e1407089845943579c/example.html"))
    val canonicalUnversionedCode = Some(Uri("https://gist.githubusercontent.com/anonymous/de6e64bd8b3b01eefa2f/raw"))

    // val canonicalVersionedDisplay = Some(Uri("https://gist.github.com/anonymous/de6e64bd8b3b01eefa2f/5e1f8c484ebbd2b0e6784942bd51bee1f780cc23"))
    val canonicalUnversionedDisplay = Some(Uri("https://gist.github.com/anonymous/de6e64bd8b3b01eefa2f"))

    "canonicalize unversioned URLs correctly" in {
      val canonicalCode = Some(Uri("https://gist.githubusercontent.com/anonymous/de6e64bd8b3b01eefa2f/raw"))
      val canonicalCodeWithFilename = Some(Uri("https://gist.githubusercontent.com/anonymous/de6e64bd8b3b01eefa2f/raw/example.html"))
      codeGist("https://gist.githubusercontent.com/anonymous/de6e64bd8b3b01eefa2f/raw") mustEqual canonicalCode
      codeGist("https://gist.githubusercontent.com/anonymous/de6e64bd8b3b01eefa2f/raw/") mustEqual canonicalCode
      codeGist("https://gist.github.com/anonymous/de6e64bd8b3b01eefa2f") mustEqual canonicalCode
      codeGist("https://gist.github.com/anonymous/de6e64bd8b3b01eefa2f/") mustEqual canonicalCode
      codeGist("https://gist.githubusercontent.com/anonymous/de6e64bd8b3b01eefa2f/raw/example.html") mustEqual canonicalCodeWithFilename

      val canonicalDisplay = Some(Uri("https://gist.github.com/anonymous/de6e64bd8b3b01eefa2f"))
      dispGist("https://gist.githubusercontent.com/anonymous/de6e64bd8b3b01eefa2f/raw") mustEqual canonicalDisplay
      dispGist("https://gist.githubusercontent.com/anonymous/de6e64bd8b3b01eefa2f/raw/") mustEqual canonicalDisplay
      dispGist("https://gist.github.com/anonymous/de6e64bd8b3b01eefa2f") mustEqual canonicalDisplay
      dispGist("https://gist.github.com/anonymous/de6e64bd8b3b01eefa2f/") mustEqual canonicalDisplay
      dispGist("https://gist.githubusercontent.com/anonymous/de6e64bd8b3b01eefa2f/raw/example.html") mustEqual canonicalCodeWithFilename
    }

    "canonicalize versioned URLs correctly" in {
      codeGist("https://gist.githubusercontent.com/anonymous/de6e64bd8b3b01eefa2f/raw/487e0f2eb3d3d39d3f4555e1407089845943579c") mustEqual canonicalVersionedCode
      dispGist("https://gist.githubusercontent.com/anonymous/de6e64bd8b3b01eefa2f/raw/487e0f2eb3d3d39d3f4555e1407089845943579c") mustEqual canonicalVersionedCode

      codeGist("https://gist.githubusercontent.com/anonymous/de6e64bd8b3b01eefa2f/raw/487e0f2eb3d3d39d3f4555e1407089845943579c/") mustEqual canonicalVersionedCode
      dispGist("https://gist.githubusercontent.com/anonymous/de6e64bd8b3b01eefa2f/raw/487e0f2eb3d3d39d3f4555e1407089845943579c/") mustEqual canonicalVersionedCode

      codeGist("https://gist.githubusercontent.com/anonymous/de6e64bd8b3b01eefa2f/raw/487e0f2eb3d3d39d3f4555e1407089845943579c/example.html") mustEqual canonicalVersionedCodeWithFilename
      dispGist("https://gist.githubusercontent.com/anonymous/de6e64bd8b3b01eefa2f/raw/487e0f2eb3d3d39d3f4555e1407089845943579c/example.html") mustEqual canonicalVersionedCodeWithFilename

      codeGist("https://gist.github.com/anonymous/de6e64bd8b3b01eefa2f/5e1f8c484ebbd2b0e6784942bd51bee1f780cc23") mustEqual canonicalUnversionedCode
      dispGist("https://gist.github.com/anonymous/de6e64bd8b3b01eefa2f/5e1f8c484ebbd2b0e6784942bd51bee1f780cc23") mustEqual canonicalUnversionedDisplay

      codeGist("https://gist.github.com/anonymous/de6e64bd8b3b01eefa2f/5e1f8c484ebbd2b0e6784942bd51bee1f780cc23/") mustEqual canonicalUnversionedCode
      dispGist("https://gist.github.com/anonymous/de6e64bd8b3b01eefa2f/5e1f8c484ebbd2b0e6784942bd51bee1f780cc23/") mustEqual canonicalUnversionedDisplay
    }
  }

  "BlOcksExample" should {
    def codeBlock(url: String) = BlOcksExample(Uri(url)).map{ _.codeUrl }
    def dispBlock(url: String) = BlOcksExample(Uri(url)).map{ _.displayUrl }

    "canonicalize unversioned URLs correctly" in {
      val canonical = Some(Uri("http://bl.ocks.org/mbostock/raw/1353700/"))

      codeBlock("http://bl.ocks.org/mbostock/1353700") mustEqual canonical
      codeBlock("http://bl.ocks.org/mbostock/1353700/") mustEqual canonical
      codeBlock("http://bl.ocks.org/mbostock/raw/1353700") mustEqual canonical
      codeBlock("http://bl.ocks.org/mbostock/raw/1353700/") mustEqual canonical
      codeBlock("http://bl.ocks.org/mbostock/raw/1353700/index.html") mustEqual canonical

      dispBlock("http://bl.ocks.org/mbostock/1353700") mustEqual canonical
      dispBlock("http://bl.ocks.org/mbostock/1353700/") mustEqual canonical
      dispBlock("http://bl.ocks.org/mbostock/raw/1353700") mustEqual canonical
      dispBlock("http://bl.ocks.org/mbostock/raw/1353700/") mustEqual canonical
      dispBlock("http://bl.ocks.org/mbostock/raw/1353700/index.html") mustEqual canonical
    }

    "canonicalize versioned URLs correctly" in {
      val canonical = Some(Uri("http://bl.ocks.org/mbostock/raw/1353700/190ad80b4eb9c5e6daa7d4f210d263cbfb6db675/"))

      codeBlock("http://bl.ocks.org/mbostock/1353700/190ad80b4eb9c5e6daa7d4f210d263cbfb6db675") mustEqual canonical
      codeBlock("http://bl.ocks.org/mbostock/1353700/190ad80b4eb9c5e6daa7d4f210d263cbfb6db675/") mustEqual canonical
      codeBlock("http://bl.ocks.org/mbostock/raw/1353700/190ad80b4eb9c5e6daa7d4f210d263cbfb6db675") mustEqual canonical
      codeBlock("http://bl.ocks.org/mbostock/raw/1353700/190ad80b4eb9c5e6daa7d4f210d263cbfb6db675/") mustEqual canonical
      codeBlock("http://bl.ocks.org/mbostock/raw/1353700/190ad80b4eb9c5e6daa7d4f210d263cbfb6db675/index.html") mustEqual canonical

      dispBlock("http://bl.ocks.org/mbostock/1353700/190ad80b4eb9c5e6daa7d4f210d263cbfb6db675") mustEqual canonical
      dispBlock("http://bl.ocks.org/mbostock/1353700/190ad80b4eb9c5e6daa7d4f210d263cbfb6db675/") mustEqual canonical
      dispBlock("http://bl.ocks.org/mbostock/raw/1353700/190ad80b4eb9c5e6daa7d4f210d263cbfb6db675") mustEqual canonical
      dispBlock("http://bl.ocks.org/mbostock/raw/1353700/190ad80b4eb9c5e6daa7d4f210d263cbfb6db675/") mustEqual canonical
      dispBlock("http://bl.ocks.org/mbostock/raw/1353700/190ad80b4eb9c5e6daa7d4f210d263cbfb6db675/index.html") mustEqual canonical
    }
  }

  "PastebinExample" should {
    def codePaste(url: String) = PastebinExample(Uri(url)).map{ _.codeUrl }
    def dispPaste(url: String) = PastebinExample(Uri(url)).map{ _.displayUrl }

    "canonicalize URLs correctly" in {
      val canonicalDisplay = Some(Uri("http://pastebin.com/wjhfEzeE"))
      val canonicalCode = Some(Uri("http://pastebin.com/raw.php?i=wjhfEzeE"))

      codePaste("http://pastebin.com/wjhfEzeE") mustEqual canonicalCode
      codePaste("http://pastebin.com/raw.php?i=wjhfEzeE") mustEqual canonicalCode

      dispPaste("http://pastebin.com/wjhfEzeE") mustEqual canonicalDisplay
      dispPaste("http://pastebin.com/raw.php?i=wjhfEzeE") mustEqual canonicalDisplay

    }

    "discard invalid URLs" in {
      codePaste("http://pastebin.com/wjhfEzeE/Bar") must beNone
      codePaste("http://pastebin.com/wjhf.EzeE") must beNone
      codePaste("http://pastebin.com/raw.php?i=wjhf.EzeE") must beNone
      codePaste("http://pastebin.com/raw.php?z=wjhfEzeE") must beNone

      dispPaste("http://pastebin.com/wjhfEzeE/Bar") must beNone
      dispPaste("http://pastebin.com/wjhf.EzeE") must beNone
      dispPaste("http://pastebin.com/raw.php?i=wjhf.EzeE") must beNone
      dispPaste("http://pastebin.com/raw.php?z=wjhfEzeE") must beNone
    }
  }
}
