package com.scalacamp.hometask.web.ops

import org.flywaydb.core.Flyway

import scala.util.Try

/**
  * Database migrations
  */
class FlywayService(jdbcUrl: String, dbUser: String, dbPassword: String) {

  private[this] val flyway = new Flyway()
  flyway.setDataSource(jdbcUrl, dbUser, dbPassword)

  def migrateDatabaseSchema(): Int = Try(flyway.migrate()).getOrElse {
    flyway.repair()
    flyway.migrate()
  }

  def dropDatabase(): Unit = flyway.clean()
}

trait FlywayIntegration {
  val flyWayService = new FlywayService(jdbcUrl, dbUser, dbPassword)

  flyWayService.migrateDatabaseSchema()
}
