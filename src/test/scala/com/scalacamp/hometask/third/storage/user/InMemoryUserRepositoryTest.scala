package com.scalacamp.hometask.third.storage.user

import com.scalacamp.hometasks.web.storage.repo.InMemoryUserRepository
import org.scalatest.FunSuite
import org.scalatest.Matchers._

class InMemoryUserRepositoryTest extends FunSuite {
  test("should register user and get him by by id and username") {
    val userRepository = new InMemoryUserRepository
    val username = "Bob"
    val address = Some("address")
    val email = "123@email.com"
    val user = userRepository.registerUser(username, address, email)
    val userById = userRepository.getById(user.id)
    val userByUsername = userRepository.getByUsername(username)
    userById.get shouldBe user
    userByUsername.get shouldBe user
  }

  test("should return empty option if user is absent") {
    val userRepository = new InMemoryUserRepository
    val userById = userRepository.getById(100500)
    val userByUsername = userRepository.getByUsername("unknown")
    userById.isEmpty shouldBe true
    userByUsername.isEmpty shouldBe true
  }
}
