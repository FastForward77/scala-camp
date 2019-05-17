package com.scalacamp.hometasks.web.service

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Route
import akka.stream.{ActorMaterializer, Materializer}

import scala.concurrent.ExecutionContext

trait HttpService extends UserComponent with DB {
  implicit lazy val system: ActorSystem = ActorSystem()
  implicit lazy val materializer: Materializer = ActorMaterializer()
  implicit lazy val ec: ExecutionContext = system.dispatcher

  lazy val userRepo: UserRepository = new UserRepository

  lazy val routes: Route = new UserRoutes(userRepo).routes
}
