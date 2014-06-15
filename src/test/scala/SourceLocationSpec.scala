import com.chrisrebert.lmvtfy.validation.SourceLocation
import org.specs2.mutable._

class SourceLocationSpec extends Specification {
  "Line number zero" should {
    "be rejected" in {
      SourceLocation(0, 2) must beNone
    }
  }
  "Negative line number" should {
    "be rejected" in {
      SourceLocation(-1, 2) must beNone
    }
  }
  "Column number zero" should {
    "be rejected" in {
      SourceLocation(2, 0).get.columnNum must beNone
    }
  }
  "Negative column number" should {
    "be rejected" in {
      SourceLocation(2, -1).get.columnNum must beNone
    }
  }
  "Valid full location" should {
    "be accepted correctly" in {
      val maybeLoc = SourceLocation(2, 5)
      maybeLoc must beSome.which{ loc => (loc.lineNum mustEqual 2) and (loc.columnNum mustEqual Some(5)) }
    }
  }
  "Location lacking a column" should {
    "be ordered correctly" in {
      SourceLocation(1, -1).get mustEqual SourceLocation(1, -1).get
      SourceLocation(1, -1).get must beLessThan(SourceLocation(1, 1).get)
      SourceLocation(1, -1).get must beLessThan(SourceLocation(1, 5).get)
      SourceLocation(1, -1).get must beLessThan(SourceLocation(2, -1).get)
      SourceLocation(1, 1).get must beLessThan(SourceLocation(2, -1).get)
      SourceLocation(1, 5).get must beLessThan(SourceLocation(2, -1).get)
      SourceLocation(2, -1).get must beLessThan(SourceLocation(2, 1).get)
      SourceLocation(2, -1).get must beLessThan(SourceLocation(2, 5).get)
    }
    "stringify correctly" in {
      SourceLocation(2, -1).get.toString mustEqual "line 2"
    }
  }
  "Locations with columns" should {
    "be ordered correctly" in {
      SourceLocation(1, 5).get must beGreaterThan(SourceLocation(1, 4).get)
      SourceLocation(1, 5).get mustEqual SourceLocation(1, 5).get
      SourceLocation(1, 5).get must beLessThan(SourceLocation(1, 6).get)
      SourceLocation(2, 5).get must beGreaterThan(SourceLocation(1, 1).get)
      SourceLocation(2, 5).get must beLessThan(SourceLocation(3, 1).get)
    }
    "stringify correctly" in {
      SourceLocation(2, 5).get.toString mustEqual "line 2, column 5"
    }
  }
}
