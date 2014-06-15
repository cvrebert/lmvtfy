package com.chrisrebert.lmvtfy.server

import akka.event.Logging
import akka.actor.ActorRef
import spray.routing._
import spray.routing.directives.DebuggingDirectives
import spray.http._


class LmvtfyActor(protected val issueCommentEventHandler: ActorRef) extends ActorWithLogging with HttpService {
  import GitHubIssuesWebHooksDirectives.authenticatedIssueOrCommentEvent

  val settings = Settings(context.system)
  override def actorRefFactory = context
  override def receive = runRoute(theOnlyRoute)

  val theOnlyRoute =
    DebuggingDirectives.logRequestResponse("get-user", Logging.InfoLevel){
    path("lmvtfy") {
      post {
        headerValueByName("X-Github-Event") { githubEvent =>
          githubEvent match {
            case "ping" => {
              log.info("Successfully received GitHub WebHook ping.")
              complete(StatusCodes.OK)
            }
            case "issues" | "issue_comment" => {
              authenticatedIssueOrCommentEvent(settings.WebHookSecretKey.toArray) { event =>
                event.message match {
                  case Some(_) => {
                    issueCommentEventHandler ! event
                    // FIXME: do throttling
                    // FIXME: ignore examples already posted in previous comments
                    complete(StatusCodes.OK)
                  }
                  case None => complete(StatusCodes.OK, "Ignoring irrelevant action")
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
