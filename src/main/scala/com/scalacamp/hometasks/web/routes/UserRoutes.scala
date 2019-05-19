package com.scalacamp.hometasks.web.routes

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.scalacamp.hometasks.web.domain.User
import com.scalacamp.hometasks.web.ops.JsonProtocol
import com.scalacamp.hometasks.web.service.UserService

import scala.concurrent.{ExecutionContext, Future}

class UserRoutes(userService: UserService[Future])(implicit ex: ExecutionContext) extends JsonProtocol {

  val routes: Route = pathPrefix("user") {
    get {
      parameter('id.as[Long]) { id =>
        print(id)
        complete(userService.getById(id))
      }
    } ~ post {
      entity(as[User]) { u =>
        complete(userService.registerUser(u.username, u.address, u.email))
      }
    }
  }
}
