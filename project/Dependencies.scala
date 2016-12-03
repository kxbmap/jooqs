import sbt.Keys._
import sbt._
import scalaprops.ScalapropsPlugin.autoImport._

object Dependencies extends AutoPlugin {

  override def trigger: PluginTrigger = allRequirements

  object autoImport {

    val jooqVersion = settingKey[String]("jOOQ version")
    val play24Version = settingKey[String]("Play framework 2.4.x version")
    val play25Version = settingKey[String]("Play framework 2.5.x version")
    val scalaTestVersion = settingKey[String]("ScalaTest version")
    val scalaCheckVersion = settingKey[String]("ScalaCheck version")
    val slf4jVersion = settingKey[String]("Slf4j version")

    val dependencies = Modules
  }

  import autoImport._

  override lazy val projectSettings: Seq[Setting[_]] = Seq(
    jooqVersion := "3.7.4",
    play24Version := "2.4.8",
    play25Version := "2.5.10",
    scalapropsVersion := "0.3.4",
    scalaTestVersion := "3.0.1",
    scalaCheckVersion := "1.13.4",
    slf4jVersion := "1.7.21",
    resolvers += Resolver.sonatypeRepo("snapshots")
  )

  object Modules {

    val core = libraryDependencies ++= Seq(
      "org.jooq" % "jooq" % jooqVersion.value,
      "com.h2database" % "h2" % "1.4.193" % "test",
      "org.slf4j" % "slf4j-simple" % slf4jVersion.value % "test",
      "org.scalatest" %% "scalatest" % scalaTestVersion.value % "test",
      "org.scalacheck" %% "scalacheck" % scalaCheckVersion.value % "test",
      "org.mockito" % "mockito-core" % "2.2.28" % "test"
    )

    val config = libraryDependencies ++= Seq(
      "org.jooq" % "jooq" % jooqVersion.value,
      "com.github.kxbmap" %% "configs" % "0.5.0-SNAPSHOT"
    )

    private def playLibs(ver: String) = Seq(
      "com.typesafe.play" %% "play-jdbc-api" % ver,
      "com.typesafe.play" %% "play-jdbc" % ver % "test"
    )

    val play24 = Seq(
      libraryDependencies ++= playLibs(play24Version.value) ++ Seq(
        "org.scalatestplus" %% "play" % "1.4.0" % "test"
      ),
      resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
    )

    val play25 =
      libraryDependencies ++= playLibs(play25Version.value) ++ Seq(
        "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % "test",
        "org.slf4j" % "slf4j-nop" % slf4jVersion.value % "test"
      )

  }

}
