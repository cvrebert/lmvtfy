package com.chrisrebert.lmvtfy.server

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.{Try,Success,Failure}
import akka.actor.ActorRef
import akka.io.IO
import akka.pattern.ask
import akka.util.{ByteString, Timeout}
import spray.can.Http
import spray.http.HttpResponse
import spray.httpx.RequestBuilding._
import com.chrisrebert.lmvtfy.ValidationRequest
import com.chrisrebert.lmvtfy.live_examples.{CompleteRawHtml, RawHtmlFragment, HtmlWithinJavaScriptWithinHtml}
import com.chrisrebert.lmvtfy.live_examples.{LiveExampleMention, JsBinUserHtml}
import com.chrisrebert.lmvtfy.util.RichResponse

object HtmlFragment {
  private val htmlPrefix = ByteString(
    """<!DOCTYPE html>
      |<html>
      |<head>
      |    <meta charset="utf-8" />
      |    <title>Untitled</title>
      |</head>
      |<body>
      |""".stripMargin)
  private val htmlSuffix = ByteString("\n</body>\n</html>")
  def apply(fragment: ByteString) = new HtmlFragment(fragment)
}
class HtmlFragment(val fragment: ByteString) extends AnyVal {
  def asCompleteHtmlDoc = HtmlFragment.htmlPrefix ++ fragment ++ HtmlFragment.htmlSuffix
}
class LiveExampleFetcher(validator: ActorRef) extends ActorWithLogging {
  implicit val timeout = Timeout(30.seconds)

  override def receive = {
    case mention: LiveExampleMention => {
      implicit val system = context.system
      val settings = Settings(context.system)
      val url = mention.example.codeUrl
      val request = Get(url) ~> addHeader("Referer", mention.example.displayUrl.toString)
      val respFuture = (IO(Http) ? request).mapTo[HttpResponse]

      // gotta block somewhere
      Try{ Await.result(respFuture, timeout.duration) } match {
        case Success(response) => {
          if (response.status.isSuccess) {
            val maybeHtmlBytes = mention.example.kind match {
              case CompleteRawHtml => Some(response.entityByteString)
              case RawHtmlFragment => Some(HtmlFragment(response.entityByteString).asCompleteHtmlDoc)
              case HtmlWithinJavaScriptWithinHtml => {
                import com.chrisrebert.lmvtfy.util.Utf8String
                response.entityUtf8String match {
                  case JsBinUserHtml(userHtml) => Some(userHtml.utf8ByteString)
                  case _ => {
                    log.error(s"Unable to extract user HTML from JS Bin page ${url}")
                    None
                  }
                }
              }
            }
            maybeHtmlBytes.foreach{ htmlBytes =>
              if (settings.DebugHtml) {
                log.info(s"Fetched HTML for ${url} :\n${htmlBytes.utf8String}\n====END HTML====")
              }
              log.info(s"Sending ValidationRequest for ${mention} with fetched HTML.")
              validator ! ValidationRequest(htmlBytes, mention)
            }
          }
          else {
            log.error(s"Failed to fetch example for ${mention}; HTTP status: ${response.status}")
          }
        }
        case Failure(exc) => log.error(exc, s"Failed to fetch example for ${mention}")
      }
    }
  }
}
