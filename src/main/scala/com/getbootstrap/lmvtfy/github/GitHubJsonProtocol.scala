package com.getbootstrap.lmvtfy.github

import spray.json.{JsonFormat, DefaultJsonProtocol}
import spray.httpx.SprayJsonSupport

case class GitHubUser(login: String)
case class IssueOrComment(
  body: String,
  user: GitHubUser
)
case class IssueOrCommentEvent(
  action: String, // issue_comment: "opened", "closed", "reopened"; issue: "created"
  //comment: Option[IssueOrComment],
  issue: IssueOrComment
)

object GitHubJsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val gitHubUserFormat = jsonFormat1(GitHubUser.apply)
  implicit val issueOrCommentFormat = jsonFormat2(IssueOrComment.apply)
  implicit val issueOrCommentEventFormat = jsonFormat2(IssueOrCommentEvent.apply)
}
