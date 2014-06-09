package com.getbootstrap.lmvtfy.server

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import spray.can.Http
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._

object Boot extends App {
  implicit val system = ActorSystem("on-spray-can")
  implicit val timeout = Timeout(5.seconds) // FIXME: ?
  // import actorSystem.dispatcher

  // FIXME: spawn actor for Html5Validator requests
  val service = system.actorOf(Props[LmvtfyActor], "lmvtfy-service")

  IO(Http) ? Http.Bind(service, interface = "0.0.0.0", port = 80)
}
