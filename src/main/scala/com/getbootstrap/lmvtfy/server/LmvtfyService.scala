package com.getbootstrap.lmvtfy.server

import akka.actor.Actor
import spray.routing._
import spray.http._

class LmvtfyActor extends Actor with Lmvtfy {
  def actorRefFactory = context

  // TODO: timeout handling
  def receive = runRoute(theOnlyRoute)
}


trait Lmvtfy extends HttpService {
  val theOnlyRoute =
    path("lmvtfy") {
      get {
        complete {
          "Foobar"
        }
      }
    }
}
