package com.scalacamp.hometasks.third.storage.user

import com.scalacamp.hometasks.third.domain.User

trait UserRepository[F[_]] {
  def registerUser(username: String): F[User]
  def getById(id: Long): F[Option[User]]
  def getByUsername(username: String): F[Option[User]]
}
