package com.chrisrebert.lmvtfy.server

import com.chrisrebert.lmvtfy.github.{IssueOrCommentEvent, GitHubJsonProtocol}
import scala.util.{Success, Failure, Try}
import spray.json._
import spray.routing.{Directive1, ValidationRejection}
import spray.routing.directives.{BasicDirectives, RouteDirectives}

trait GitHubIssuesWebHooksDirectives {
  import RouteDirectives.reject
  import BasicDirectives.provide
  import HubSignatureDirectives.stringEntityMatchingHubSignature
  import GitHubJsonProtocol._

  def authenticatedIssueOrCommentEvent(secretKey: Array[Byte]): Directive1[IssueOrCommentEvent] = stringEntityMatchingHubSignature(secretKey).flatMap{ entityJsonString =>
    Try{ entityJsonString.parseJson.convertTo[IssueOrCommentEvent] } match {
      case Failure(err) => reject(ValidationRejection("JSON either malformed or does not match expected schema!"))
      case Success(event) => provide(event)
    }
  }
}

object GitHubIssuesWebHooksDirectives extends GitHubIssuesWebHooksDirectives
