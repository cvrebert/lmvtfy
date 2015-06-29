import org.specs2.mutable._
import com.chrisrebert.lmvtfy.validation.{SourceLocation, SourceSpan}

class SourceSpanSpec extends Specification {
  "Start and end both invalid" should {
    "be rejected" in {
      SourceSpan(-1, 1, -2, 2) must beNone
    }
  }
  "Only end valid" should {
    "be reinterpreted as only start" in {
      SourceSpan(-1, -1, 1, 2) must beSome.which{ span =>
        (span.start mustEqual SourceLocation(1, 2).get) &&
          (span.end must beNone)
      }
    }
  }
  "Only start valid" should {
    "be accepted properly" in {
      SourceSpan(2, 3, -1, -1) must beSome.which{ span =>
        (span.start mustEqual SourceLocation(2, 3).get) &&
        (span.end must beNone)
      }
    }
    "stringify correctly" in {
      SourceSpan(2, 3, -1, -1).get.toString mustEqual "line 2, column 3"
    }
  }
  "Both start and end valid and in correct order" should {
    "be accepted properly" in {
      SourceSpan(2, 3, 4, 5) must beSome.which{ span =>
        (span.start mustEqual SourceLocation(2, 3).get) &&
          (span.end mustEqual Some(SourceLocation(4, 5).get))
      }
    }
    "stringify correctly" in {
      SourceSpan(2, 3, 4, 5).get.toString mustEqual "line 2, column 3 thru line 4, column 5"
    }
  }
  "Valid start and end in correct order on the same line" should {
    "stringify using a special more compact less redundant format" in {
      SourceSpan(2, 4, 2, 7).get.toString mustEqual "line 2, column 4 thru column 7"
    }
  }
  "Both start and end valid but in wrong order" should {
    "get flipped" in {
      SourceSpan(4, 5, 2, 3) must beSome.which{ span =>
        (span.start mustEqual SourceLocation(2, 3).get) &&
          (span.end mustEqual Some(SourceLocation(4, 5).get))
      }
    }
  }
  "Identical valid start and end" should {
    "be simplified to having no end" in {
      SourceSpan(2, 3, 2, 3) mustEqual SourceSpan(2, 3, -1, -1)
      SourceSpan(2, -1, 2, -1) mustEqual SourceSpan(2, -1, -1, -1)
    }
  }
}
