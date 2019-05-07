organization in ThisBuild := "com.scalacamp"

name := "hometasks"

version in ThisBuild := "0.0.1-SNAPSHOT"

scalaVersion in ThisBuild := "2.12.8"

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.12" % "3.0.8-RC2" % "test"
)