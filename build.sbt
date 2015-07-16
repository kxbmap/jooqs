name := "jooqs"
description in ThisBuild := ""

version in ThisBuild := "0.1.0-SNAPSHOT"
scalaVersion in ThisBuild := "2.11.7"
organization in ThisBuild := "com.github.kxbmap"

scalacOptions in ThisBuild ++= Seq(
  "-deprecation",
  "-unchecked",
  "-feature",
  "-Xlint",
  "-Xexperimental"
)

fork in ThisBuild := true
parallelExecution in ThisBuild := false

lazy val generateSyntax = taskKey[Unit]("generate syntax object")

lazy val core = project.settings(
  name := "jooqs-core",
  generateSyntax := {
    SyntaxGenerator((scalaSource in Compile).value, streams.value.log)
  },
  compile in Compile <<= (compile in Compile).dependsOn(generateSyntax),
  libraryDependencies ++= Seq(
    "org.jooq" % "jooq" % "3.6.2",
    "com.h2database" % "h2" % "1.4.187" % "test",
    "com.zaxxer" % "HikariCP" % "2.3.9" % "test",
    "org.slf4j" % "slf4j-simple" % "1.7.12" % "test"
  ) ++ testDependencies,
  javaOptions in Test ++= Seq(
    "-Dorg.slf4j.simpleLogger.defaultLogLevel=warn"
  )
)

lazy val play = project.settings(
  name := "jooqs-play",
  libraryDependencies ++= Seq(
    "com.typesafe.play" %% "play-jdbc" % "2.4.2",
    "com.typesafe.play" %% "play-specs2" % "2.4.2" % "test"
  )
).dependsOn(core)

lazy val testDependencies = Seq(
  "org.scalatest" %% "scalatest" % "2.2.5" % "test",
  "org.scalacheck" %% "scalacheck" % "1.12.4" % "test",
  "org.mockito" % "mockito-core" % "2.0.26-beta" % "test"
)
