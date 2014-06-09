package com.getbootstrap.lmvtfy.server

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import java.security.{NoSuchAlgorithmException, InvalidKeyException, SignatureException}
import java.security.MessageDigest

object HmacSha1 {
  private val HmacSha1Algorithm = "HmacSHA1"

  implicit class HexByteArray(array: Array[Byte]) {
    import javax.xml.bind.DatatypeConverter
    def asHexBytes: String = DatatypeConverter.printHexBinary(array).toLowerCase
  }
}

case class HmacSha1(mac: Array[Byte], secretKey: Array[Byte], data: Array[Byte]) {
  import HmacSha1.HmacSha1Algorithm
  import HmacSha1.HexByteArray

  @throws[NoSuchAlgorithmException]("if HMAC-SHA1 is not supported")
  @throws[InvalidKeyException]("if the secret key is malformed")
  @throws[SignatureException]("under unknown circumstances")
  private lazy val correct: Array[Byte] = {
    val key = new SecretKeySpec(secretKey, HmacSha1Algorithm)
    val mac = Mac.getInstance(HmacSha1Algorithm)
    mac.init(key)
    mac.doFinal(data)
  }

  lazy val isValid: Boolean = MessageDigest.isEqual(mac, correct)

  def givenHex = mac.asHexBytes
  def correctHex = correct.asHexBytes
}
