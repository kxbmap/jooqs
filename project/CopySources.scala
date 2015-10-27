import sbt.Keys._
import sbt._

object CopySources extends AutoPlugin {

  override def requires: Plugins = plugins.JvmPlugin

  object autoImport {

    val sourcesToCopies = taskKey[Seq[(File, File)]]("sources to copies")
    val resourcesToCopies = taskKey[Seq[(File, File)]]("resources to copies")

    def copies(sources: Seq[File], sourceDirs: Seq[File], managedDir: File): Seq[(File, File)] =
      for {
        s <- sources if s.isFile
        d <- sourceDirs
        r <- IO.relativize(d, s)
      } yield s -> managedDir / r

  }

  import autoImport._

  override lazy val projectSettings: Seq[Setting[_]] =
    Seq(Compile, Test).flatMap(inConfig(_)(Seq(
      sourceGenerators <+= Def.task {
        IO.copy(sourcesToCopies.value).toSeq
      },
      resourceGenerators <+= Def.task {
        IO.copy(resourcesToCopies.value).toSeq
      }
    )))

}
