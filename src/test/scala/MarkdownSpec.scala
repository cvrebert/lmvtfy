import org.specs2.mutable._
import com.chrisrebert.lmvtfy.validation._
import com.chrisrebert.lmvtfy.validation.markdown.MarkdownRenderer

class MarkdownSpec extends Specification {
  def singletonMsg(part: MessagePart) = Seq(ValidationMessage(None, Seq(part)))

  "Simple validation message" should {
    "be markdownified correctly" in {
      val parts = Seq(CodeText("foo"), PlainText(" the bar in the "), CodeText("baz"), PlainText(" quixotically."))
      MarkdownRenderer.markdownFor(Seq(ValidationMessage(None, parts))) mustEqual "* ```` foo ```` the bar in the ```` baz ```` quixotically."
    }
  }
  "Many backticks in code part of message" should {
    "be sanitized" in {
      val markdownOne = MarkdownRenderer.markdownFor(singletonMsg(CodeText("This ```` is technical")))
      markdownOne mustEqual "* ```` This [backticks] is technical ````"

      val markdownTwo = MarkdownRenderer.markdownFor(singletonMsg(CodeText("This ````` is technical")))
      markdownTwo mustEqual "* ```` This [backticks] is technical ````"
    }
    "be left alone if not too numerous" in {
      val markdown = MarkdownRenderer.markdownFor(singletonMsg(CodeText("This ``` is technical")))
      markdown mustEqual "* ```` This ``` is technical ````"
    }
  }
  "Backticks in plain text part of message" should {
    "be backslash-escaped" in {
      MarkdownRenderer.markdownFor(singletonMsg(PlainText("` tick `` it back `"))) mustEqual "* \\` tick \\`\\` it back \\`"
    }
  }
  "Multiple messages" should {
    "be listified properly" in {
      val msgs = Seq(
        ValidationMessage(None, Seq(PlainText("One"))),
        ValidationMessage(None, Seq(PlainText("Two"))),
        ValidationMessage(None, Seq(PlainText("Three")))
      )
      MarkdownRenderer.markdownFor(msgs) mustEqual "* One\n* Two\n* Three"
    }
  }
  "Messages without source location info" should {
    "be rendered properly" in {
      MarkdownRenderer.markdownFor(singletonMsg(PlainText("One"))) mustEqual "* One"
    }
  }
  "Messages with source location info" should {
    "be rendered properly" in {
      val markdownOne = MarkdownRenderer.markdownFor(Seq(ValidationMessage(SourceSpan(2, 3, 4, 5), Seq(PlainText("Foo")))))
      markdownOne mustEqual "* line 2, column 3 thru line 4, column 5: Foo"

      val markdownTwo = MarkdownRenderer.markdownFor(Seq(ValidationMessage(SourceSpan(2, 3, -1, -1), Seq(PlainText("Bar")))))
      markdownTwo mustEqual "* line 2, column 3: Bar"
    }
  }
}
