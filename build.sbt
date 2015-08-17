name := "jooqs"

commonSettings
disablePublishSettings

lazy val jooqVersion = settingKey[String]("jOOQ version")
lazy val playVersion = settingKey[String]("Play framework version")
lazy val scalaTestVersion = settingKey[String]("ScalaTest version")
lazy val configsVersion = settingKey[String]("configs version")

lazy val generateSyntax = taskKey[Unit]("generate syntax object")

lazy val commonSettings = Seq[Setting[_]](
  description := "",
  version := "0.1.0-SNAPSHOT",
  organization := "com.github.kxbmap",
  scalaVersion := "2.11.7",
  jooqVersion := "3.6.2",
  configsVersion := "0.3.0-SNAPSHOT",
  scalaTestVersion := (CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, 12)) => "2.2.5-M2"
    case _             => "2.2.5"
  }),
  scalacOptions ++= Seq(
    "-deprecation",
    "-unchecked",
    "-feature",
    "-Xlint",
    "-Xexperimental"
  ),
  parallelExecution in Test := false,
  updateOptions := updateOptions.value.withCachedResolution(true)
)

lazy val crossVersionSettings = Seq[Setting[_]](
  crossScalaVersions += "2.12.0-M2"
)

lazy val scalaTestSettings = Seq[Setting[_]](
  libraryDependencies ++= Seq(
    "org.scalatest" %% "scalatest" % scalaTestVersion.value % "test",
    "org.scalacheck" %% "scalacheck" % "1.12.4" % "test",
    "org.mockito" % "mockito-core" % "2.0.31-beta" % "test"
  )
)


lazy val core = project.settings(
  name := "jooqs-core",
  commonSettings,
  crossVersionSettings,
  scalaTestSettings,
  generateSyntax := {
    SyntaxGenerator((scalaSource in Compile).value, streams.value.log)
  },
  compile in Compile <<= (compile in Compile).dependsOn(generateSyntax),
  libraryDependencies ++= Seq(
    "org.jooq" % "jooq" % jooqVersion.value,
    "com.h2database" % "h2" % "1.4.188" % "test",
    "org.slf4j" % "slf4j-simple" % "1.7.12" % "test"
  )
)

lazy val config = project.settings(
  name := "jooqs-config",
  commonSettings,
  crossVersionSettings,
  scalapropsSettings,
  scalapropsVersion := "0.1.13",
  libraryDependencies ++= Seq(
    "org.jooq" % "jooq" % jooqVersion.value,
    "com.github.kxbmap" %% "configs" % configsVersion.value,
    "com.github.kxbmap" %% "configs-macro" % configsVersion.value % "provided"
  ),
  resolvers += Resolver.sonatypeRepo("snapshots")
)

lazy val play = project.settings(
  name := "jooqs-play",
  commonSettings,
  playVersion := "2.4.2",
  libraryDependencies ++= Seq(
    "com.typesafe.play" %% "play-jdbc-api" % playVersion.value,
    "com.typesafe.play" %% "play-jdbc" % playVersion.value % "test",
    "com.typesafe.play" %% "play-specs2" % playVersion.value % "test"
  ),
  resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
).dependsOn(core, config)
