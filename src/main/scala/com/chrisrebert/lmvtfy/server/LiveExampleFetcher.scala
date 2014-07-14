package com.chrisrebert.lmvtfy.server

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.{Try,Success,Failure}
import akka.actor.ActorRef
import akka.io.IO
import akka.pattern.ask
import akka.util.{ByteString, Timeout}
import spray.can.Http
import spray.http.HttpResponse
import spray.httpx.RequestBuilding._
import com.chrisrebert.lmvtfy.ValidationRequest
import com.chrisrebert.lmvtfy.live_examples.{LiveExampleMention, RawHtml, JsonContainingHtml}
import com.chrisrebert.lmvtfy.live_examples.jsbin.JsBin

class LiveExampleFetcher(validator: ActorRef) extends ActorWithLogging {
  implicit val timeout = Timeout(30.seconds)

  override def receive = {
    case mention: LiveExampleMention => {
      implicit val system = context.system
      val respFuture = (IO(Http) ? Get(mention.example.codeUrl)).mapTo[HttpResponse]

      // gotta block somewhere
      Try{ Await.result(respFuture, timeout.duration) } match {
        case Success(response) => {
          if (response.status.isSuccess) {
            val maybeHtmlBytes = mention.example.kind match {
              case RawHtml => Some(response.entity.data.toByteString)
              case JsonContainingHtml => {
                import spray.httpx.unmarshalling._
                import spray.httpx.SprayJsonSupport._
                import com.chrisrebert.lmvtfy.live_examples.jsbin.JsBinJsonProtocol._
                import com.chrisrebert.lmvtfy.util.Utf8String
                response.entity.as[JsBin] match {
                  case Left(err) => {
                    log.error(s"Error deserializing JS Bin JSON: ${err}")
                    None
                  }
                  case Right(actualBin) => Some(ByteString(actualBin.html.utf8Bytes))
                }
              }
            }
            maybeHtmlBytes.foreach{ htmlBytes =>
              log.info(s"Sending ValidationRequest for ${mention} with fetched HTML.")
              validator ! ValidationRequest(htmlBytes, mention)
            }
          }
          else {
            log.error(s"Failed to fetch example for ${mention}; HTTP status: ${response.status}")
          }
        }
        case Failure(exc) => log.error(exc, s"Failed to fetch example for ${mention}")
      }
    }
  }
}
