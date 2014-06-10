package com.getbootstrap.lmvtfy.server

import com.getbootstrap.lmvtfy.github.{IssueOrCommentEvent, GitHubJsonProtocol}
import scala.util.{Success, Failure, Try}
import spray.json._
import spray.routing.{Directive1, ValidationRejection}
import spray.routing.directives.{BasicDirectives, RouteDirectives}

trait GitHubIssuesWebHooksDirectives extends HubSignatureDirectives {
  import RouteDirectives.reject
  import BasicDirectives.provide
  import GitHubJsonProtocol._

  def authenticatedIssueOrCommentEvent(secretKey: Array[Byte]): Directive1[IssueOrCommentEvent] = entityMatchingHubSignature(secretKey).flatMap{ entityJsonString =>
    Try{ entityJsonString.parseJson.convertTo[IssueOrCommentEvent] } match {
      case Failure(err) => reject(ValidationRejection("JSON either malformed or does not match expected schema!"))
      case Success(event) => provide(event)
    }
  }
}
