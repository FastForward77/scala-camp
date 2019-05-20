package com.scalacamp.hometasks.web.routes

import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import com.scalacamp.hometasks.second.Validator
import com.scalacamp.hometasks.web.domain.User
import com.scalacamp.hometasks.web.service.UserService
import com.typesafe.scalalogging.Logger

import scala.concurrent.{ExecutionContext, Future}


class UserRoutes(userService: UserService[Future])(implicit ex: ExecutionContext) {
  val logger = Logger(this.getClass)

  implicit def exceptionHandler = ExceptionHandler {
    case e: Exception =>
      extractUri { uri =>
        logger.error("Exception: ", e)
        complete(HttpResponse(InternalServerError, entity = e.getMessage))
      }
  }

  import Validator._
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import com.scalacamp.hometasks.web.ops.UserJsonProtocol._

  val routes: Route = Route.seal(pathPrefix("user") {
    get {
      parameter('id.as[Long]) { id =>
        complete(userService.getById(id))
      }
    } ~ post {
      entity(as[User]) { u =>
        complete(u.username validate match {
          case Right(_) => userService.registerUser(u.username, u.address, u.email)
            .map {
              case Right(res) => s"""{"id": ${res.id}}"""
              case Left(message) => message
            }
          case Left(message) => message
        })
      }
    }
  })
}
