package com.scalacamp.hometasks.web.config

import com.typesafe.config.ConfigFactory

trait HttpConfig {
  def interface: String

  def port: Int
}

trait DatabaseConfig {
  def profile: String

  def port: Int

  def driverName: String

  def databaseName: String

  def jdbcUrl: String

  def user: String

  def password: String

  def numThreads: Int
}

class AppConfig {
  private val config =  ConfigFactory.load("application.conf")

  val applicationName: String = config.getString("application.name")

  lazy val httpConfig: HttpConfig = new HttpConfig {
    private val httpConfig = config.getConfig("http")

    lazy val interface: String = httpConfig.getString("interface")
    lazy val port: Int = httpConfig.getInt("port")
  }

  lazy val databaseConfig: DatabaseConfig = new DatabaseConfig {
    private val databaseConfig = config.getConfig("db")

    lazy val profile: String = databaseConfig.getString("profile")
    lazy val port: Int = databaseConfig.getInt("port")
    lazy val driverName: String = databaseConfig.getString("driver")
    lazy val databaseName: String = databaseConfig.getString("databaseName")
    lazy val jdbcUrl: String = databaseConfig.getString("jdbcUrl")
    lazy val user: String = databaseConfig.getString("user")
    lazy val password: String = databaseConfig.getString("password")
    lazy val numThreads: Int = databaseConfig.getInt("numThreads")
  }
}
