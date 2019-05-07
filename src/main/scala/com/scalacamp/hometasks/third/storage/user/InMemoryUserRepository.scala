package com.scalacamp.hometasks.third.storage.user

import java.util.concurrent.atomic.AtomicInteger

import cats.Id
import com.scalacamp.hometasks.third.domain.User

import scala.collection.mutable

class InMemoryUserRepository extends UserRepository[Id] {
  val currentUserId = new AtomicInteger(10000)
  val storage: mutable.Map[Long, User] = mutable.Map.empty

  override def registerUser(username: String): Id[User] = {
    val user = User(currentUserId.getAndIncrement(), username)
    storage.put(user.id, user)
    user
  }

  override def getById(id: Long): Id[Option[User]] = storage.get(id)

  override def getByUsername(username: String): Id[Option[User]] = storage.values.find(_.username == username)
}
