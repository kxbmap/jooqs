import sbt.Keys._
import sbt._

object CopySources extends AutoPlugin {

  override def requires: Plugins = plugins.JvmPlugin

  object autoImport {
    val copySourcesProject = settingKey[ProjectReference]("copySourcesProject")
    val sourcesToCopies = taskKey[Map[File, File]]("sources to copies")
    val resourcesToCopies = taskKey[Map[File, File]]("resources to copies")
  }

  import autoImport._

  override lazy val projectSettings: Seq[Setting[_]] =
    Seq(Compile, Test).flatMap(inConfig(_)(Seq(
      sourcesToCopies := Def.taskDyn {
        val prj = copySourcesProject.value
        Def.task {
          copies((sources in prj).value, (sourceDirectories in prj).value, sourceManaged.value)
        }
      }.value,
      resourcesToCopies := Def.taskDyn {
        val prj = copySourcesProject.value
        Def.task {
          copies((resources in prj).value, (resourceDirectories in prj).value, resourceManaged.value)
        }
      }.value,
      sourceGenerators += Def.task {
        IO.copy(sourcesToCopies.value).toSeq
      }.taskValue,
      resourceGenerators += Def.task {
        IO.copy(resourcesToCopies.value).toSeq
      }.taskValue
    )))

  private def copies(sources: Seq[File], sourceDirs: Seq[File], managedDir: File): Map[File, File] =
    (for {
      s <- sources if s.isFile
      d <- sourceDirs
      r <- IO.relativize(d, s)
    } yield s -> managedDir / r).toMap

}
