package com.chrisrebert.lmvtfy.github

import spray.json.DefaultJsonProtocol
import org.eclipse.egit.github.core.RepositoryId

case class GitHubRepository(val full_name: String) extends AnyVal {
  def id: RepositoryId = RepositoryId.createFromId(full_name)
}
case class GitHubUser(val login: String) extends AnyVal
case class IssueOrComment(
  number: Option[Int], // issue number
  body: String,
  user: GitHubUser
)
case class IssueOrCommentEvent(
  action: String, // issue_comment: "opened", "closed", "reopened"; issue: "created"
  repository: GitHubRepository,
  comment: Option[IssueOrComment],
  issue: IssueOrComment
) {
  def message: Option[IssueOrComment] = action match {
    case "opened" => Some(issue)
    case "created" => comment
    case _ => None
  }
  private def issueNumber: Option[IssueNumber] = issue.number.flatMap{ IssueNumber(_) }
  def gitHubIssue: Option[GitHubIssue] = issueNumber.map{ GitHubIssue(repository, _) }
}

object GitHubJsonProtocol extends DefaultJsonProtocol {
  implicit val gitHubRepoFormat = jsonFormat1(GitHubRepository.apply)
  implicit val gitHubUserFormat = jsonFormat1(GitHubUser.apply)
  implicit val issueOrCommentFormat = jsonFormat3(IssueOrComment.apply)
  implicit val issueOrCommentEventFormat = jsonFormat4(IssueOrCommentEvent.apply)
}
