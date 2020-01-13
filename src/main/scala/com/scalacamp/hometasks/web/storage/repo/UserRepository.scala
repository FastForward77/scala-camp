package com.scalacamp.hometasks.web.storage.repo

import com.scalacamp.hometasks.web.domain.User


trait UserRepository[F[_]] {
  def registerUser(username: String, address: Option[String], email: String): F[User]
  def getById(id: Long): F[Option[User]]
  def getByUsername(username: String): F[Option[User]]
}
