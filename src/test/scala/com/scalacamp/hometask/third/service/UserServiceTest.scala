package com.scalacamp.hometask.third.service

import com.scalacamp.hometasks.third.service.UserService
import com.scalacamp.hometasks.third.storage.user.InMemoryUserRepository
import org.scalatest.Matchers._
import org.scalatest.{EitherValues, FunSuite}


class UserServiceTest extends FunSuite with EitherValues {
  test("should successful add first user") {
    val userService = new UserService(new InMemoryUserRepository)
    val username = "Bob"
    val resultUser = userService.registerUser(username).right.value
    resultUser.username shouldBe username
    resultUser.id shouldBe 10000L
  }

  test("should fail on adding user with existing username") {
    val userService = new UserService(new InMemoryUserRepository)
    val username = "Bob"
    userService.registerUser(username)
    userService.registerUser(username).isLeft shouldBe true
  }
}
