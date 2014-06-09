package com.getbootstrap.lmvtfy.server

import java.nio.charset.Charset
import akka.actor.Actor
import spray.routing._
import spray.http._
import spray.json._
import com.getbootstrap.lmvtfy.github._

class LmvtfyActor extends Actor with Lmvtfy {
  def actorRefFactory = context

  // TODO: timeout handling
  def receive = runRoute(theOnlyRoute)
}


trait Lmvtfy extends HttpService {
  import GitHubJsonProtocol._

  val theOnlyRoute =
    path("lmvtfy") {
      post {
        headerValueByName("X-Github-Event") { githubEvent =>
          headerValueByName("X-Hub-Signature") { hmacHex =>
            val hmacBytes = javax.xml.bind.DatatypeConverter.parseHexBinary(hmacHex)
            entity(as[Array[Byte]]) { rawBody =>
              val FOOBARDDDDD = Array(1,2,3,4,5).map{_.toByte} // FIXME
              val hmac = new HmacSha1(mac = hmacBytes, secretKey = FOOBARDDDDD, data = rawBody)
              if (false && !hmac.isValid) {// FIXME
                complete(StatusCodes.Forbidden, "HMAC verification failed!")
              }
              else {
                formField("payload") { payload =>
                  System.out.println("RAW JSON:", payload)
                  githubEvent match {
                    case "ping" => {
                      System.out.println("Pong.")
                      complete(StatusCodes.OK)
                    }
                    case "issues" | "issue_comment" => {
                      val event = payload.parseJson.convertTo[IssueOrCommentEvent]
                      event.action match {
                        case "opened" | "created" => {
                          val comment = event.comment.getOrElse(event.issue)
                          val markdown = comment.body
                          val username = comment.user.login
                          System.out.println("EVENT: ", event.action)
                          System.out.println("USERNAME: ", username)
                          System.out.println("COMMENT BODY: ", markdown)
                          // FIXME: DO ACTUAL WORK
                          complete(StatusCodes.OK)
                        }
                        case _ => complete(StatusCodes.OK, "Ignoring irrelevant action")
                      }
                    }
                    case _ => complete(StatusCodes.BadRequest, "Unexpected event type")
                  }
                }
              }
            }
          }
        }
      }
    }
}
