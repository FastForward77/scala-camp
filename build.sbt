organization in ThisBuild := "com.scalacamp"

name := "hometasks"

version in ThisBuild := "0.0.1-SNAPSHOT"

scalaVersion in ThisBuild := "2.12.8"

libraryDependencies ++= {
  val akkaVersion = "2.5.22"
  val akkaHttpVersion = "10.1.8"
  val flywayVersion = "3.2.1"
  val scalaTestVersion = "3.0.5"
  val logbackVersion = "1.2.3"
  val slickVersion = "3.3.0"
  Seq(
    "org.typelevel" %% "cats-core" % "1.6.0",
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
    "com.typesafe.slick" %% "slick" % slickVersion,
    "com.typesafe.slick" %% "slick-hikaricp" % slickVersion,
    "com.typesafe" % "config" % "1.3.4",
    "org.flywaydb" % "flyway-core" % "3.2.1",
    "org.postgresql" % "postgresql" % "9.4.1211",
    "org.slf4j" % "slf4j-nop" % "1.6.4",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "com.h2database" % "h2" % "1.4.192" % "test",
    "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % "test",
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % "test",
    "org.scalatest" % "scalatest_2.12" % "3.0.8-RC2" % "test"
  )
}