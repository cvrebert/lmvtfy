package com.chrisrebert.lmvtfy.server

import akka.actor.ActorRef
import spray.routing._
import spray.http._


class LmvtfyActor(protected val issueCommentEventHandler: ActorRef) extends ActorWithLogging with HttpService {
  import GitHubIssuesWebHooksDirectives.authenticatedIssueOrCommentEvent

  val settings = Settings(context.system)
  override def actorRefFactory = context
  override def receive = runRoute(theOnlyRoute)

  val theOnlyRoute =
    pathPrefix("lmvtfy") { pathEndOrSingleSlash {
      get {
        complete(StatusCodes.OK, "Hi! LMVTFY is online.")
      } ~
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
    }}
}
