package com.getbootstrap.lmvtfy.server

import java.nio.charset.Charset
import akka.actor.Actor
import spray.routing._
import spray.http._
import spray.json._
import com.getbootstrap.lmvtfy.HmacSha1

class LmvtfyActor extends Actor with Lmvtfy {
  def actorRefFactory = context

  // TODO: timeout handling
  def receive = runRoute(theOnlyRoute)
}


trait Lmvtfy extends HttpService {
  import GitHubJsonProtocol._

  val hmacHexCharset = Charset.forName("US-ASCII")

  val theOnlyRoute =
    path("lmvtfy") {
      get {
        headerValueByName("X-Github-Event") { githubEvent =>
          headerValueByName("X-Hub-Signature") { hmacHex =>
            val hmacBytes = javax.xml.bind.DatatypeConverter.parseHexBinary(hmacHex)
            entity(as[Array[Byte]]) { rawBody =>
              val FOOBARDDDDD = Array(1,2,3,4,5).map{_.toByte} // FIXME
              val hmac = new HmacSha1(mac = hmacBytes, secretKey = FOOBARDDDDD, data = rawBody)
              if (!hmac.isValid) {
                complete(StatusCodes.Forbidden, "HMAC verification failed!")
              }
              else {
                formField("payload") { payload =>
                  githubEvent match {
                    case "issues" | "issue_comment" => {
                      val event = payload.parseJson.convertTo[IssueOrCommentEvent]
                      event.action match {
                        case "opened" | "created" => {
                          val comment = event.comment.getOrElse(event.issue)
                          val markdown = comment.body
                          val username = comment.user.login
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

case class GitHubUser(login: String)
case class IssueOrComment(
  body: String,
  user: GitHubUser
)
case class IssueOrCommentEvent(
  action: String, // issue_comment: "opened", "closed", "reopened"; issue: "created"
  comment: Option[IssueOrComment],
  issue: IssueOrComment
)

object GitHubJsonProtocol extends DefaultJsonProtocol {
  implicit val gitHubUserFormat = jsonFormat1(GitHubUser.apply)
  implicit val issueOrCommentFormat = jsonFormat2(IssueOrComment.apply)
  implicit val issueOrCommentEventFormat: JsonFormat[IssueOrCommentEvent] = jsonFormat3(IssueOrCommentEvent.apply)
}
