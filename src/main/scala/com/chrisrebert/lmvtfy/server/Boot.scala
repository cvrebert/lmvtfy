package com.chrisrebert.lmvtfy.server

import scala.concurrent.duration._
import akka.actor.{ActorSystem, Props}
import akka.io.IO
import spray.can.Http
import akka.pattern.ask
import akka.routing.SmallestMailboxPool
import akka.util.Timeout
import com.chrisrebert.lmvtfy.github.GitHubIssueCommenter


object Boot extends App {
  implicit val system = ActorSystem("on-spray-can")
  implicit val timeout = Timeout(5.seconds) // FIXME: ?
  // import actorSystem.dispatcher

  val commenter = system.actorOf(Props(classOf[GitHubIssueCommenter]))
  val localValidator = system.actorOf(Props(classOf[ValidatorSingletonActor], commenter), "validator-service")
  val exampleFetcherPool = system.actorOf(SmallestMailboxPool(5).props(Props(classOf[LiveExampleFetcher], localValidator)), "example-fetcher-pool")
  val issueCommentEventHandler = system.actorOf(Props(classOf[IssueCommentEventHandler], exampleFetcherPool), "issue-comment-event-handler")
  val webService = system.actorOf(Props(classOf[LmvtfyActor], issueCommentEventHandler), "lmvtfy-service")

  IO(Http) ? Http.Bind(webService, interface = "0.0.0.0", port = 80)
}
