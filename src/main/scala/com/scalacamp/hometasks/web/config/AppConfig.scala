package com.scalacamp.hometasks.web.config

import com.typesafe.config.ConfigFactory

trait HttpConfig {
  def interface: String

  def port: Int
}

trait DatabaseConfig {
  def port: Int

  def databaseName: String

  def jdbcUrl: String

  def user: String

  def password: String
}

class AppConfig {
  private val config =  ConfigFactory.load("application.conf")

  val applicationName: String = config.getString("application.name")

  lazy val httpConfig: HttpConfig = new HttpConfig {
    private val httpConfig = config.getConfig("http")

    lazy val interface: String = httpConfig.getString("interface")
    lazy val port: Int = httpConfig.getInt("port")
  }

  def databaseConfig(dbName: String): DatabaseConfig = new DatabaseConfig {
    private val databaseConfig = config.getConfig(s"db.${dbName}.db")

    lazy val port: Int = databaseConfig.getInt("port")
    lazy val databaseName: String = databaseConfig.getString("databaseName")
    lazy val jdbcUrl: String = databaseConfig.getString("url")
    lazy val user: String = databaseConfig.getString("user")
    lazy val password: String = databaseConfig.getString("password")
  }
}
