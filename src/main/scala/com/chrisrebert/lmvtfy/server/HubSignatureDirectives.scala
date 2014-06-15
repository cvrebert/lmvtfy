package com.chrisrebert.lmvtfy.server

import scala.util.Try
import spray.routing.{Directive1, MalformedHeaderRejection, ValidationRejection}
import spray.routing.directives.{BasicDirectives, HeaderDirectives, RouteDirectives, MarshallingDirectives}
import com.chrisrebert.lmvtfy.util.Utf8String

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

  def stringEntityMatchingHubSignature(secretKey: Array[Byte]): Directive1[String] = hubSignature.flatMap { signature =>
    stringEntity.flatMap { string =>
      val bytesEntity = string.utf8Bytes
      val hmac = new HmacSha1(mac = signature, secretKey = secretKey, data = bytesEntity)
      if (hmac.isValid) {
        provide(string)
      }
      else {
        reject(ValidationRejection("Incorrect HMAC"))
      }
    }
  }
}

object HubSignatureDirectives extends HubSignatureDirectives
