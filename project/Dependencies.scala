import sbt.Keys._
import sbt._
import scalaprops.ScalapropsPlugin.autoImport._

object Dependencies extends AutoPlugin {

  override def trigger = allRequirements

  object autoImport {

    val jooqVersion = settingKey[String]("jOOQ version")
    val playVersion = settingKey[String]("Play framework version")
    val scalaTestVersion = settingKey[String]("ScalaTest version")

    object dependencies {

      val core = libraryDependencies ++= Seq(
        "org.jooq" % "jooq" % jooqVersion.value,
        "com.h2database" % "h2" % "1.4.189" % "test",
        "org.slf4j" % "slf4j-simple" % "1.7.12" % "test",
        "org.scalatest" %% "scalatest" % scalaTestVersion.value % "test",
        "org.scalacheck" %% "scalacheck" % "1.12.5" % "test",
        "org.mockito" % "mockito-core" % "2.0.31-beta" % "test"
      )

      val config = libraryDependencies ++= Seq(
        "org.jooq" % "jooq" % jooqVersion.value,
        "com.github.kxbmap" %% "configs" % "0.3.0"
      )

      val play = Seq(
        libraryDependencies ++= Seq(
          "com.typesafe.play" %% "play-jdbc-api" % playVersion.value,
          "com.typesafe.play" %% "play-jdbc" % playVersion.value % "test",
          "com.typesafe.play" %% "play-specs2" % playVersion.value % "test"
        ),
        resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      )

    }

  }

  import autoImport._

  override lazy val projectSettings: Seq[Setting[_]] = Seq(
    jooqVersion := "3.7.0",
    playVersion := "2.4.3",
    scalaTestVersion := (CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, 12)) => "2.2.5-M2"
      case _             => "2.2.5"
    }),
    scalapropsVersion := "0.1.14"
  )

}
