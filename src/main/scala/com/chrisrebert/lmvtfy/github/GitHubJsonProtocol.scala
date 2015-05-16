package com.chrisrebert.lmvtfy.github

import spray.json._
import com.jcabi.github.Coordinates.{Simple=>RepoId}

case class GitHubRepository(fullName: String) extends AnyVal {
  def id: RepoId = new RepoId(fullName)
}
case class GitHubUser(username: String) extends AnyVal
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
  implicit object RepoJsonFormat extends JsonFormat[GitHubRepository] {
    override def write(repo: GitHubRepository) = JsObject("full_name" -> JsString(repo.fullName))
    override def read(value: JsValue) = {
      value.asJsObject.getFields("full_name") match {
        case Seq(JsString(fullName)) => new GitHubRepository(fullName)
        case _ => throw new DeserializationException("GitHubRepository expected")
      }
    }
  }
  implicit object UserFormat extends JsonFormat[GitHubUser] {
    override def write(user: GitHubUser) = JsObject("login" -> JsString(user.username))
    override def read(value: JsValue) = {
      value.asJsObject.getFields("login") match {
        case Seq(JsString(username)) => new GitHubUser(username)
      }
    }
  }
  implicit val issueOrCommentFormat = jsonFormat3(IssueOrComment.apply)
  implicit val issueOrCommentEventFormat = jsonFormat4(IssueOrCommentEvent.apply)
}
