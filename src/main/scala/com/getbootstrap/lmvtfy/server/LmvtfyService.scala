package com.getbootstrap.lmvtfy.server

import akka.actor.Actor
import spray.routing._
import spray.http._
import com.getbootstrap.lmvtfy.github._
import akka.event.Logging
import spray.routing.directives.DebuggingDirectives
import scala.util.{Try, Success, Failure}

class LmvtfyActor extends Actor with Lmvtfy {
  def actorRefFactory = context

  // TODO: timeout handling
  def receive = runRoute(theOnlyRoute)
}


trait Lmvtfy extends HttpService {
  import GitHubJsonProtocol._

  val theOnlyRoute =
    DebuggingDirectives.logRequestResponse("get-user", Logging.InfoLevel){
    import spray.httpx.SprayJsonSupport._
    path("lmvtfy") {
      post {
        headerValueByName("X-Github-Event") { githubEvent =>
          headerValueByName("X-Hub-Signature") { hmacStr =>
            val parts = hmacStr.split('=')
            parts match {
              case Array("sha1", hmacHex) => {
                Try{ javax.xml.bind.DatatypeConverter.parseHexBinary(hmacHex) } match {
                  case Failure(_) => complete(StatusCodes.Forbidden, "Malformed HMAC hex!")
                  case Success(hmacBytes) => {
                    //entity(as[Array[Byte]]) { rawBody =>
                      val FOOBARDDDDD = Array(1,2,3,4,5).map{_.toByte} // FIXME
                      val hmac = new HmacSha1(mac = hmacBytes, secretKey = FOOBARDDDDD, data = FOOBARDDDDD/*rawBody*/)
                      if (false && !hmac.isValid) {// FIXME
                        complete(StatusCodes.Forbidden, "HMAC verification failed!")
                      }
                      else {
                        githubEvent match {
                          case "ping" => {
                            System.out.println("Pong.")
                            complete(StatusCodes.OK)
                          }
                          case "issues" | "issue_comment" => { context =>
                            val entityBytes = context.request.entity.data.toByteArray
                            entity(as[IssueOrCommentEvent]) { event =>
                              event.action match {
                                case "opened" | "created" => {
                                  System.out.println(event)
                                  // FIXME: DO ACTUAL WORK
                                  complete(StatusCodes.OK)
                                }
                                case _ => complete(StatusCodes.OK, "Ignoring irrelevant action")
                              }
                            }
                          }
                          case _ => complete(StatusCodes.BadRequest, "Unexpected event type")
                        }
                      }
                    //}
                  }
                }
              }
              case _ => complete(StatusCodes.Forbidden, "Malformed HMAC!")
            }
          }
        }
      }
    }
    }
}
