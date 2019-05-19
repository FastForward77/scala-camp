package com.scalacamp.hometasks.web.storage.repo

import com.scalacamp.hometasks.web.domain.User
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.language.{existentials, postfixOps}
import scala.concurrent.{ExecutionContext, Future}


class DbUserRepository(val dbConfig: DatabaseConfig[JdbcProfile])(implicit ex: ExecutionContext)
  extends UserRepository[Future] {

  import dbConfig.profile.api._
  private val db = dbConfig.db

  class UserTable(tag: Tag) extends Table[User](tag, "users") {
    val id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    val username = column[String]("username")
    val address = column[Option[String]]("address")
    val email = column[String]("email")

    def * = (id, username, address, email) <> (User.tupled, User.unapply)
  }

  val users = new TableQuery(tag => new UserTable(tag))

  override def registerUser(username: String, address: Option[String], email: String): Future[User] = {
//    db.run()
  ???
  }

  override def getById(id: Long): Future[Option[User]] = db.run(users.filter(user => user.id === id).result.headOption)

  override def getByUsername(username: String): Future[Option[User]] = ???
}
