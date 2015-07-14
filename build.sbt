name := "jooqs"
description := ""

version in ThisBuild := "0.1.0-SNAPSHOT"
scalaVersion in ThisBuild := "2.11.7"
organization in ThisBuild := "com.github.kxbmap"

commonSettings
Publish.disable

lazy val commonSettings = Seq(
  scalacOptions ++= Seq(
    "-feature",
    "-Xlint",
    "-Xexperimental"
  ),
  libraryDependencies ++= Seq(
    "org.scalatest" %% "scalatest" % "2.2.5" % "test",
    "org.scalacheck" %% "scalacheck" % "1.12.4" % "test",
    "org.mockito" % "mockito-core" % "2.0.26-beta" % "test"
  ),
  fork := true,
  parallelExecution in Test := false
)

def module(id: String): Project = Project(id, file(id)).settings(
  name := s"jooqs-$id",
  commonSettings,
  Publish.settings
)

lazy val generateSyntax = taskKey[Unit]("generate syntax object")

lazy val core = module("core").settings(
  description := "",
  libraryDependencies ++= Seq(
    "org.jooq" % "jooq" % "3.6.2",
    "com.h2database" % "h2" % "1.4.187" % "test",
    "com.zaxxer" % "HikariCP" % "2.3.9" % "test",
    "org.slf4j" % "slf4j-simple" % "1.7.12" % "test"
  ),
  javaOptions in Test ++= Seq(
    "-Dorg.slf4j.simpleLogger.defaultLogLevel=warn"
  ),
  generateSyntax := {
    SyntaxGenerator((scalaSource in Compile).value, streams.value.log)
  },
  compile in Compile <<= (compile in Compile).dependsOn(generateSyntax)
)

lazy val play = module("play").settings(
  description := "",
  libraryDependencies ++= Seq(
    "com.typesafe.play" %% "play-jdbc" % "2.4.2",
    "com.typesafe.play" %% "play-specs2" % "2.4.2" % "test"
  )
).dependsOn(core)
