package com.chrisrebert.lmvtfy.server

import scala.util.{Try,Success,Failure}
import spray.routing.{Directive1, MalformedHeaderRejection, MalformedRequestContentRejection, ValidationRejection}
import spray.routing.directives.{BasicDirectives, HeaderDirectives, RouteDirectives, MarshallingDirectives}
import com.chrisrebert.lmvtfy.util.{HmacSha1,Utf8ByteArray}

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

  private val bytesEntity = entity(as[Array[Byte]])

  def stringEntityMatchingHubSignature(secretKey: Array[Byte]): Directive1[String] = hubSignature.flatMap { signature =>
    bytesEntity.flatMap { dataBytes =>
      val hmac = new HmacSha1(mac = signature, secretKey = secretKey, data = dataBytes)
      if (hmac.isValid) {
        dataBytes.utf8String match {
          case Success(string) => provide(string)
          case Failure(exc) => reject(MalformedRequestContentRejection("Request body is not valid UTF-8", Some(exc)))
        }
      }
      else {
        reject(ValidationRejection("Incorrect HMAC"))
      }
    }
  }
}

object HubSignatureDirectives extends HubSignatureDirectives
