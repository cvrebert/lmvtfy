package com.chrisrebert.lmvtfy.server

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.{Try,Success,Failure}
import akka.actor.ActorRef
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import spray.can.Http
import spray.http.HttpResponse
import spray.httpx.RequestBuilding._
import com.chrisrebert.lmvtfy.ValidationRequest
import com.chrisrebert.lmvtfy.live_examples.LiveExampleMention

class LiveExampleFetcher(validator: ActorRef) extends ActorWithLogging {
  implicit val timeout = Timeout(30.seconds)

  override def receive = {
    case mention: LiveExampleMention => {
      implicit val system = context.system
      val respFuture = (IO(Http) ? Get(mention.example.url)).mapTo[HttpResponse]

      // gotta block somewhere
      Try{ Await.result(respFuture, timeout.duration) } match {
        case Success(response) => {
          if (response.status.isSuccess) {
            val htmlBytes = response.entity.data.toByteString
            log.info(s"Sending ValidationRequest for ${mention} with fetched HTML.")
            validator ! ValidationRequest(htmlBytes, mention)
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
