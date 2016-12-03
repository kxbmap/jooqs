import BuildUtil._
import sbt.Keys._
import sbt._

object Common extends AutoPlugin {

  override def trigger: PluginTrigger = allRequirements

  override lazy val projectSettings: Seq[Setting[_]] = Seq(
    scalaVersion := "2.11.8",
    description := "",
    organization := "com.github.kxbmap",
    scalacOptions ++= Seq(
      "-deprecation",
      "-unchecked",
      "-feature",
      "-Xlint",
      "-language:higherKinds",
      "-language:implicitConversions",
      "-language:experimental.macros"
    ),
    scalacOptions ++= byScalaVersion {
      case (2, 12) => Seq(
        "-opt:l:method"
      )
      case (2, 11) => Seq(
        // lambda syntax for SAM types
        "-Xexperimental"
      )
    }.value,
    parallelExecution in Test := false,
    updateOptions := updateOptions.value.withCachedResolution(true)
  )

  object autoImport {

    val crossVersionSettings: Seq[Setting[_]] = Seq(
      crossScalaVersions := Seq("2.11.8", "2.12.0")
    )
  }

}
