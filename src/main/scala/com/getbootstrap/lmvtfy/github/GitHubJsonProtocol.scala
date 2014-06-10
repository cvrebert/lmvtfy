package com.getbootstrap.lmvtfy.github

import spray.json.DefaultJsonProtocol

case class GitHubRepository(full_name: String)
case class GitHubUser(login: String)
case class IssueOrComment(
  body: String,
  user: GitHubUser
)
case class IssueOrCommentEvent(
  action: String, // issue_comment: "opened", "closed", "reopened"; issue: "created"
  repository: GitHubRepository,
  comment: Option[IssueOrComment],
  issue: IssueOrComment
)

object GitHubJsonProtocol extends DefaultJsonProtocol {
  implicit val gitHubRepoFormat = jsonFormat1(GitHubRepository.apply)
  implicit val gitHubUserFormat = jsonFormat1(GitHubUser.apply)
  implicit val issueOrCommentFormat = jsonFormat2(IssueOrComment.apply)
  implicit val issueOrCommentEventFormat = jsonFormat4(IssueOrCommentEvent.apply)
}
