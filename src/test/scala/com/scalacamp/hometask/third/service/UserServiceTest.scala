package com.scalacamp.hometask.third.service

import com.scalacamp.hometasks.web.service.UserService
import com.scalacamp.hometasks.web.storage.repo.InMemoryUserRepository
import org.scalatest.Matchers._
import org.scalatest.{EitherValues, FunSuite}


class UserServiceTest extends FunSuite with EitherValues {
  test("should successful add first user") {
    val userService = new UserService(new InMemoryUserRepository)
    val username = "Bob"
    val address = Some("address")
    val email = "123@mail.com"
    val resultUser = userService.registerUser(username, address, email).right.value
    resultUser.username shouldBe username
    resultUser.id shouldBe 10000L
  }

  test("should fail on adding user with existing username") {
    val userService = new UserService(new InMemoryUserRepository)
    val username = "Bob"
    val address = Some("address")
    val email = "123@email.com"
    userService.registerUser(username, address, email)
    userService.registerUser(username, address, email).isLeft shouldBe true
  }
}
