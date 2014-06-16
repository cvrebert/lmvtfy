package com.chrisrebert.lmvtfy.server

import scala.concurrent.duration._
import scala.util.Try
import akka.actor.{ActorSystem, Props}
import akka.io.IO
import spray.can.Http
import akka.pattern.ask
import akka.routing.SmallestMailboxPool
import akka.util.Timeout
import com.chrisrebert.lmvtfy.github.GitHubIssueCommenter


object Boot extends App {
  val arguments = args.toSeq
  val maybePort = arguments match {
    case Seq(portStr: String) => {
      Try{ portStr.toInt }.toOption
    }
    case _ => None
  }
  maybePort match {
    case Some(port) => run(port)
    case _ => {
      System.err.println("USAGE: lmvtfy <port-number>")
      System.exit(1)
    }
  }

  def run(port: Int) {
    implicit val system = ActorSystem("on-spray-can")
    // import actorSystem.dispatcher

    val commenter = system.actorOf(Props(classOf[GitHubIssueCommenter]))
    val localValidator = system.actorOf(Props(classOf[ValidatorSingletonActor], commenter), "validator-service")
    val exampleFetcherPool = system.actorOf(SmallestMailboxPool(5).props(Props(classOf[LiveExampleFetcher], localValidator)), "example-fetcher-pool")
    val issueCommentEventHandler = system.actorOf(Props(classOf[IssueCommentEventHandler], exampleFetcherPool), "issue-comment-event-handler")
    val webService = system.actorOf(Props(classOf[LmvtfyActor], issueCommentEventHandler), "lmvtfy-service")

    implicit val timeout = Timeout(15.seconds)
    IO(Http) ? Http.Bind(webService, interface = "0.0.0.0", port = port)
  }
}
