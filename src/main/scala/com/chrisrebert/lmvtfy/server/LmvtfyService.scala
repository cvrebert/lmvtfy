package com.chrisrebert.lmvtfy.server

import akka.event.Logging
import akka.actor.ActorRef
import spray.routing._
import spray.routing.directives.DebuggingDirectives
import spray.http._
import com.chrisrebert.lmvtfy.util.Utf8String

class LmvtfyActor(protected override val issueCommentEventHandler: ActorRef) extends ActorWithLogging with Lmvtfy {
  override def actorRefFactory = context

  // TODO: timeout handling
  override def receive = runRoute(theOnlyRoute)
}

trait Lmvtfy extends HttpService {
  import GitHubIssuesWebHooksDirectives.authenticatedIssueOrCommentEvent

  protected def issueCommentEventHandler: ActorRef

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
                if (event.repository.fullName == "cvrebert/lmvtfy-test") { // FIXME
                  event.message match {
                    case Some(_) => {
                      issueCommentEventHandler ! event
                      // FIXME: needs to ignore its own comments
                      // FIXME: do throttling
                      // FIXME: ignore examples already posted in previous comments
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
