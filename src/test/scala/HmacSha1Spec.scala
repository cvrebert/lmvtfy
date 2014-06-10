import com.getbootstrap.lmvtfy.server.HmacSha1
import com.getbootstrap.lmvtfy.util.Utf8String
import org.specs2.mutable._

class HmacSha1Spec extends Specification {
  "The HMAC-SHA1 checker" should {
    val key = "key".utf8Bytes
    val data = "The quick brown fox jumps over the lazy dog".utf8Bytes
    val correctHmacHex = "de7c9b85b8b78aa6bc8a7a36f70a90701c9db4d9"
    val correctHmac: Array[Byte] = Array(0xde,0x7c,0x9b,0x85,0xb8,0xb7,0x8a,0xa6,0xbc,0x8a,0x7a,0x36,0xf7,0x0a,0x90,0x70,0x1c,0x9d,0xb4,0xd9).map{_.toByte}
    val validHmac = new HmacSha1(mac = correctHmac, secretKey = key, data = data)
    val badHmac: Array[Byte] = Array(0xab, 0xe6, 0x89, 0x0c, 0xac, 0x1b, 0xab, 0xd8, 0x9d, 0xb8, 0xa6, 0x89, 0xd0, 0x53, 0x97, 0x7a, 0xb7, 0x0f, 0xc7, 0x74).map{_.toByte}
    val badHmacHex = "abe6890cac1babd89db8a689d053977ab70fc774"
    val invalidHmac = new HmacSha1(mac = badHmac, secretKey = key, data = data)

    "accept valid HMAC" in {
      validHmac.isValid mustEqual true
    }
    "reject invalid HMAC" in {
      invalidHmac.isValid mustEqual false
    }
    "display results in hexadecimal representation correctly" in {
      validHmac.correctHex mustEqual correctHmacHex
      validHmac.givenHex mustEqual correctHmacHex

      invalidHmac.givenHex mustEqual badHmacHex
      validHmac.correctHex mustEqual correctHmacHex
    }
  }
}
