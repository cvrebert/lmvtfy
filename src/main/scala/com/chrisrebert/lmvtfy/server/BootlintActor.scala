package com.chrisrebert.lmvtfy.server

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.{Try,Success,Failure}
import akka.actor.ActorRef
import akka.io.IO
import akka.pattern.ask
import akka.util.{ByteString, Timeout}
import spray.can.Http
import spray.http.{HttpCharsets, Uri, MediaTypes, HttpResponse}
import spray.httpx.RequestBuilding._
import spray.json._
import com.chrisrebert.lmvtfy.{MarkdownAboutBootstrap, ValidationRequest, ValidationResult}
import com.chrisrebert.lmvtfy.bootlint.{BootlintProblem, BootlintJsonProtocol, MarkdownRenderer}
import com.chrisrebert.lmvtfy.live_examples.{JsFiddleExample, BootplyExample, LiveExample, LiveExampleMention}
import com.chrisrebert.lmvtfy.util.Utf8ByteString


class BootlintActor(commenter: ActorRef) extends ActorWithLogging {
  implicit val timeout = Timeout(30.seconds)

  private lazy val bootlintUrl: Uri = {
    val settings = Settings(context.system)
    val dockerEnvVar = s"BOOTLINT_PORT_${settings.BootlintPort}_TCP_ADDR"
    val host = System.getenv(dockerEnvVar) match {
      case null => Uri.NamedHost("localhost")
      case ipAddr => Uri.IPv4Host(ipAddr)
    }
    val authority = Uri.Authority(host, settings.BootlintPort)
    val url = Uri(Uri.httpScheme(securedConnection = false), authority, Uri.Path.Empty)
    log.info(s"Using Bootlint URL: ${url}")
    url
  }

  private def lintFor(html: ByteString, mention: LiveExampleMention): Seq[BootlintProblem] = {
    implicit val system = context.system
    val settings = Settings(context.system)

    val entity = html.asUtf8HtmlHttpEntity
    val request = Post(bootlintUrl, entity) ~> addHeader("Accept", MediaTypes.`application/json`.toString())

    val respFuture = (IO(Http) ? request).mapTo[HttpResponse]
    Try{ Await.result(respFuture, timeout.duration) } match {
      case Success(response) => {
        if (response.status.isSuccess) {
          import BootlintJsonProtocol._
          val jsonString = response.entity.asString(HttpCharsets.`UTF-8`)
          Try { jsonString.parseJson.convertTo[Seq[BootlintProblem]] } match {
            case Failure(exc) => {
              log.error(s"Bootlint response JSON either malformed or did not conform to expected schema")
              Nil
            }
            case Success(lintProblems) => lintProblems
          }
        }
        else {
          log.error(s"Failed to fetch Bootlint for ${mention}; HTTP status: ${response.status}")
          Nil
        }
      }
      case Failure(exc) => {
        log.error(exc, s"Failed to fetch Bootlint for ${mention}")
        Nil
      }
    }
  }

  private val bootplyLintIdToIgnore = "W002"
  private val jsFiddleLintIdsToIgnore = Set("W001", "W002", "W003")
  private def withoutIrrelevantLints(lintProblems: Seq[BootlintProblem], example: LiveExample): Seq[BootlintProblem] = {
    example match {
      case _:BootplyExample => lintProblems.filter{ _.id != bootplyLintIdToIgnore }
      case _:JsFiddleExample => lintProblems.filter{ problem => !(jsFiddleLintIdsToIgnore contains problem.id) }
      case _ => lintProblems
    }
  }

  override def receive = {
    case req@ValidationRequest(htmlBytes, mention) => {
      val lintProblems = withoutIrrelevantLints(lintFor(htmlBytes, mention), mention.example)
      if (lintProblems.isEmpty) {
        log.info(s"No Bootlint problems for ${mention}")
      }
      else {
        log.info(s"${lintProblems.length} Bootlint problems for ${mention}")
        val lintsAsMarkdown = MarkdownAboutBootstrap(MarkdownRenderer.markdownFor(lintProblems))
        commenter ! ValidationResult(lintsAsMarkdown, mention)
      }
    }
  }
}
