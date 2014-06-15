package com.chrisrebert.lmvtfy.server

import scala.util.Try
import spray.routing.{Directive1, MalformedHeaderRejection, ValidationRejection}
import spray.routing.directives.{BasicDirectives, HeaderDirectives, RouteDirectives, MarshallingDirectives}
import spray.util.LoggingContext
import com.chrisrebert.lmvtfy.util.{HmacSha1, Utf8String}

trait HubSignatureDirectives  {

  import BasicDirectives.provide
  import HeaderDirectives.headerValueByName
  import RouteDirectives.reject
  import MarshallingDirectives.{entity, as}

  private val xHubSignature = "X-Hub-Signature"
  private val hubSignatureHeaderValue = headerValueByName(xHubSignature)

  val hubSignature: Directive1[Array[Byte]] = hubSignatureHeaderValue.flatMap { algoEqHex =>
    val bytesFromHexOption = algoEqHex.split('=') match {
      case Array("sha1", hex) => Try{ javax.xml.bind.DatatypeConverter.parseHexBinary(hex) }.toOption
      case _ => None
    }
    bytesFromHexOption match {
      case Some(bytesFromHex) => provide(bytesFromHex)
      case None => reject(MalformedHeaderRejection(xHubSignature, "Malformed HMAC"))
    }
  }

  private val stringEntity = entity(as[String])

  def stringEntityMatchingHubSignature(secretKey: Array[Byte])(implicit log: LoggingContext): Directive1[String] = hubSignature.flatMap { signature =>
    stringEntity.flatMap { string =>
      val bytesEntity = string.utf8Bytes
      val hmac = new HmacSha1(mac = signature, secretKey = secretKey, data = bytesEntity)
      if (hmac.isValid) {
        provide(string)
      }
      else {
        // FIXME: remove once debugged
        val base64data = javax.xml.bind.DatatypeConverter.parseHexBinary(string)
        log.error(s"Incorrect HMAC; expected ${hmac.correctHex}; got ${hmac.givenHex}; data as Base64 was ${base64data}")
        reject(ValidationRejection("Incorrect HMAC"))
      }
    }
  }
}

object HubSignatureDirectives extends HubSignatureDirectives
