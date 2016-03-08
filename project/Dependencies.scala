import sbt.Keys._
import sbt._
import scalaprops.ScalapropsPlugin.autoImport._

object Dependencies extends AutoPlugin {

  override def trigger = allRequirements

  object autoImport {

    val jooqVersion = settingKey[String]("jOOQ version")
    val play24Version = settingKey[String]("Play framework 2.4.x version")
    val play25Version = settingKey[String]("Play framework 2.5.x version")
    val scalaTestVersion = settingKey[String]("ScalaTest version")
    val scalaCheckVersion = settingKey[String]("ScalaCheck version")

    val dependencies = Modules
  }

  import autoImport._

  override lazy val projectSettings: Seq[Setting[_]] = Seq(
    jooqVersion := "3.7.3",
    play24Version := "2.4.6",
    play25Version := "2.5.0",
    scalapropsVersion := "0.2.1",
    scalaTestVersion := (CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, 12)) => "2.2.5-M3"
      case _             => "2.2.6"
    }),
    scalaCheckVersion := "1.13.0"
  )

  object Modules {

    val core = libraryDependencies ++= Seq(
      "org.jooq" % "jooq" % jooqVersion.value,
      "com.h2database" % "h2" % "1.4.191" % "test",
      "org.slf4j" % "slf4j-simple" % "1.7.18" % "test",
      "org.scalatest" %% "scalatest" % scalaTestVersion.value % "test",
      "org.scalacheck" %% "scalacheck" % scalaCheckVersion.value % "test",
      "org.mockito" % "mockito-core" % "2.0.43-beta" % "test"
    )

    val config = libraryDependencies ++= Seq(
      "org.jooq" % "jooq" % jooqVersion.value,
      "com.github.kxbmap" %% "configs" % "0.3.0"
    )

    private def playLibs(ver: String) = Seq(
      "com.typesafe.play" %% "play-jdbc-api" % ver,
      "com.typesafe.play" %% "play-jdbc" % ver % "test",
      "com.typesafe.play" %% "play-specs2" % ver % "test"
    )

    val play24 = Seq(
      libraryDependencies ++= playLibs(play24Version.value),
      resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
    )

    val play25 = libraryDependencies ++= playLibs(play25Version.value)

  }

}
