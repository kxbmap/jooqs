import sbt._

object SyntaxGenerator {

  val pkg = "com.github.kxbmap.jooqs"
  val obj = "syntax"

  val classes = Seq[OpsClass](
    FieldOpsClass
  )

  def apply(scalaSource: File, log: Logger): Unit = {
    val file = pkg.split('.').foldLeft(scalaSource)(_ / _) / s"$obj.scala"
    if (file.exists()) {
      val old = IO.read(file)
      val updated = updateSource(old)
      if (updated == old) {
        log.debug(s"No changed $file")
      } else {
        log.info(s"Updating $file")
        IO.write(file, updated)
      }
    } else {
      log.info(s"Creating $file")
      IO.write(file, render)
    }
  }

  def updateSource(old: String): String = {
    val delimiter = "////"
    val oldChunks = old.split(delimiter)
    val newChunks = render.split(delimiter)
    if (oldChunks.length != newChunks.length) {
      sys.error("different number of chunks in old and new source")
    }
    val updatedChunks =
      for {
        ((o, n), i) <- oldChunks.zip(newChunks).zipWithIndex
      } yield {
        val useOld = i % 2 == 1
        if (useOld) o else n
      }
    updatedChunks.mkString(delimiter)
  }

  def render: String =
    s"""package $pkg
       |
       |////
       |
       |import org.jooq._
       |import org.jooq.impl.DSL
       |
       |////
       |
       |object $obj {
       |
       |  ////
       |
       |  ////
       |
       |${classes.map(_.render).mkString("", "\n\n", "\n").indent}
       |}
       |""".stripMargin


  abstract class OpsClass(name: String, paramType: String, typeParams: Seq[String] = Nil) {
    def tps = typeParams match {
      case Nil => ""
      case tps => tps.mkString("[", ", ", "]")
    }

    def members: Seq[String]

    def render: String =
      s"""implicit class $name$tps(private val self: $paramType) extends AnyVal {
         |
         |  ////
         |
         |  ////
         |
         |${members.mkString("", "\n", "\n").indent}
         |}
         |""".stripMargin
  }


  object FieldOpsClass extends OpsClass("FieldOps", "Field[T]", Seq("T")) {
    def members: Seq[String] = {
      def condOps(ops: Seq[(String, String)], types: Seq[String]) = for {
        (op, m) <- ops
        t <- types
      } yield
        s"""def $op(other: $t): Condition = self.$m(other)
           |""".stripMargin

      val standardCondOps = condOps(
        Seq(
          "===" -> "equal",
          "=!=" -> "notEqual",
          "<>"  -> "notEqual",
          "<"   -> "lessThan",
          "<="  -> "lessOrEqual",
          ">"   -> "greaterThan",
          ">="  -> "greaterOrEqual"
        ),
        Seq(
          "T",
          "Field[T]",
          "Select[_ <: Record1[T]]",
          "QuantifiedSelect[_ <: Record1[T]]"
        ))

      val distinctCondOps = condOps(
        Seq(
          "<=>" -> "isNotDistinctFrom"
        ),
        Seq(
          "T",
          "Field[T]"
        ))

      standardCondOps ++ distinctCondOps
    }
  }


  implicit class IndentOps(val self: String) extends AnyVal {
    def indent: String = indent(1)

    def indent(n: Int): String = self.linesIterator.map {
      case ""   => ""
      case line => "  " * n + line
    }.mkString("\n")
  }

}
