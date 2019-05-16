package com.scalacamp.hometask.web

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.typesafe.scalalogging.Logger

import scala.io.StdIn

object Server extends App {
  val logger = Logger(this.getClass)

  implicit val system = ActorSystem("my-system")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  def route = path("hello") {
    get {
      complete("Hello, World!")
    }
  }

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

  logger.info(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  StdIn.readLine() // let it run until user presses return
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done
}
