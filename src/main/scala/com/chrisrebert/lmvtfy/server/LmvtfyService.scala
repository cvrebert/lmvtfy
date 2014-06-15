package com.chrisrebert.lmvtfy.server

import akka.actor.Actor
import spray.routing._
import spray.routing.directives.DebuggingDirectives
import spray.http._
import com.chrisrebert.lmvtfy.util.Utf8String
import akka.event.Logging

class LmvtfyActor extends Actor with Lmvtfy {
  def actorRefFactory = context

  // TODO: timeout handling
  def receive = runRoute(theOnlyRoute)
}

trait Lmvtfy extends HttpService {
  import GitHubIssuesWebHooksDirectives.authenticatedIssueOrCommentEvent

  val theOnlyRoute =
    DebuggingDirectives.logRequestResponse("get-user", Logging.InfoLevel){
    path("lmvtfy") {
      post {
        headerValueByName("X-Github-Event") { githubEvent =>
          githubEvent match {
            case "ping" => {
              System.out.println("Pong.")
              complete(StatusCodes.OK)
            }
            case "issues" | "issue_comment" => {
              val secretKey = "abcdefg".utf8Bytes // FIXME
              authenticatedIssueOrCommentEvent(secretKey) { event =>
                if (event.repository.full_name == "cvrebert/lmvtfy-test") {
                  event.message match {
                    case Some(message) => {
                      // FIXME: DO ACTUAL WORK
                      complete(StatusCodes.OK)
                    }
                    case None => complete(StatusCodes.OK, "Ignoring irrelevant action")
                  }
                }
                else {
                  complete(StatusCodes.Forbidden, "Event is from an unexpected repository")
                }
              }
            }
            case _ => complete(StatusCodes.BadRequest, "Unexpected event type")
          }
        }
      }
    }
    }
}