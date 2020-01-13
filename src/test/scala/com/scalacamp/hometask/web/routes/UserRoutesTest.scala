package com.scalacamp.hometask.web.routes

import akka.http.javadsl.model.StatusCodes
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.{HttpRequest, MessageEntity}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.scalacamp.hometasks.web.domain.User
import com.scalacamp.hometasks.web.routes.UserRoutes
import com.scalacamp.hometasks.web.service.UserService
import com.scalacamp.hometasks.web.storage.repo.DbUserRepository
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile
import cats.implicits._

class UserRoutesTest extends WordSpec with Matchers with ScalaFutures with ScalatestRouteTest {
  val dbConfig = DatabaseConfig.forConfig[JdbcProfile]("db.h2")
  val userService = new UserService(new DbUserRepository(dbConfig))
  val routes = new UserRoutes(userService).routes

  "UserRoutes" should {
    "return no users if no present (GET /user)" in {
      val request = HttpRequest(uri = "/user?id=100500")
      request ~> routes ~> check {
        status should ===(StatusCodes.OK)
        entityAs[String] should ===("")
      }
    }

    "return existing user by id (GET /user)" in {
      val request = HttpRequest(uri = "/user?id=1")
      request ~> routes ~> check {
        status should ===(StatusCodes.OK)
        entityAs[String] should ===("""{"address":"address","email":"123@mail.com","id":1,"username":"test_user"}""")
      }
    }

    "be able to add users (POST /user)" in {

      import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
      import com.scalacamp.hometasks.web.ops.UserJsonProtocol._

      val user = User(1, "Bob", Some("address"), "123@email.com")
      val userEntity = Marshal(user).to[MessageEntity].futureValue

      val request = Post("/user").withEntity(userEntity)

      request ~> routes ~> check {
        status should ===(StatusCodes.OK)

        //presumably this assert will be unstable, I'm not sure that id always will be 5
        entityAs[String] should ===("""{"id": 5}""")
      }
    }
  }
}
