import sbt.Path._
import sbt.{File, IO, Logger}

object SyntaxGenerator {

  val pkg = "com.github.kxbmap.jooqs"
  val obj = "syntax"

  lazy val classes = Seq(
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
      IO.write(file, renderAll)
    }
  }


  def renderAll: String =
    s"""package $pkg
       |
       |import org.jooq._
       |import org.jooq.impl.DSL
       |
       |object $obj {
       |
       |  //// generation:start
       |
       |${classes.map(_.render).mkString("\n\n").indent}
       |
       |  //// generation:end
       |}
       |""".stripMargin


  def updateSource(old: String): String = {
    val lines = old.lines.toVector
    val s = lines.indexWhere(_.matches("""^\s*//// generation:start$"""))
    if (s == -1) sys.error("missing start generation marker")
    val e = lines.indexWhere(_.matches("""^\s*//// generation:end$"""), s)
    if (e == -1) sys.error("missing end generation marker")

    val sb = new StringBuilder(old.length)
    lines.take(s + 1).addString(sb, "", "\n", "\n")
    updateOpsClasses(lines.slice(s + 1, e), sb)
    lines.drop(e).addString(sb, "", "\n", "\n")
    sb.toString()
  }

  def updateOpsClasses(lines: Vector[String], sb: StringBuilder): Unit = {
    val start = """^\s*//// start:(.+)$""".r
    def end(name: String) = s"^\\s*//// end:${name.replace("[", "\\[")}$$"

    val starts = lines.zipWithIndex.collect {
      case (start(name), i) => name -> i
    }
    val oldSources = (starts :+ ("sentinel", lines.size)).sliding(2).collect {
      case Vector((name, s), (_, next)) =>
        lines.indexWhere(_.matches(end(name)), s) match {
          case -1             => sys.error(s"missing end marker: $name start: $s")
          case e if e >= next => sys.error(s"invalid end marker position: $name start: $s, end: $e, next: $next")
          case e              => name -> lines.slice(s, e + 1).mkString("", "\n", "\n")
        }
    }.toMap

    classes.iterator.map { c =>
      oldSources.get(c.name).fold(c.render.indent)(c.updateSource)
    }.addString(sb, "\n", "\n\n", "\n")
  }


  abstract class OpsClass(tpe: String, paramType: String) {
    def name: String = tpe.takeWhile(_ != '[')

    def members: Seq[String]

    def render: String =
      s"""//// start:$name
         |implicit class $tpe(private val self: $paramType) extends AnyVal {
         |
         |  ////
         |
         |  ////
         |
         |${members.mkString("", "\n", "\n").indent}
         |}
         |//// end:$name
         |""".stripMargin

    def updateSource(old: String): String = {
      val delimiter = "////"
      val oldChunks = old.split(delimiter)
      val newChunks = render.indent.split(delimiter)
      if (oldChunks.length != newChunks.length) {
        sys.error("different number of chunks in old and new source")
      }
      val updatedChunks =
        for {
          ((o, n), i) <- oldChunks.zip(newChunks).zipWithIndex
        } yield {
          val useOld = i % 2 == 0
          if (useOld) o else n
        }
      updatedChunks.mkString(delimiter)
    }
  }


  object FieldOpsClass extends OpsClass("FieldOps[T]", "Field[T]") {
    lazy val members: Seq[String] = {
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

    def indent(n: Int): String = self.lines.map {
      case ""   => ""
      case line => "  " * n + line
    }.mkString("\n")
  }

}
