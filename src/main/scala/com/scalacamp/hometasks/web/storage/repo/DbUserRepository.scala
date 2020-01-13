package com.scalacamp.hometasks.web.storage.repo

import com.scalacamp.hometasks.first.RetryUtil
import com.scalacamp.hometasks.web.domain.User
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.language.{existentials, postfixOps}
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._


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

  private val users = new TableQuery(tag => new UserTable(tag))
  private val insertQuery = users returning users.map(_.id) into ((user, id) => user.copy(id = id))

  override def registerUser(username: String, address: Option[String], email: String): Future[User] = {
    val action = insertQuery += User(0, username, address, email)
    // do we really need this? what success condition should be used?
    //RetryUtil.retry[User](() => db.run(action), user => true, List(0.seconds, 1.seconds, 2.seconds))
    db.run(action)
  }

  override def getById(id: Long): Future[Option[User]] = db.run(users.filter(user => user.id === id).result.headOption)

  override def getByUsername(username: String): Future[Option[User]] = db.run(users.filter(user => user.username === username).result.headOption)
}
