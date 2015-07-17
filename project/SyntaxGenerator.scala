import sbt.Path._
import sbt.{File, IO, Logger}

object SyntaxGenerator {

  val pkg = "com.github.kxbmap.jooqs"
  val obj = "syntax"

  lazy val classes = Seq(
    ConditionOpsClass,
    FieldOpsClass,
    NumberFieldOpsClass
  ) ++
    (1 to 22).map(new RecordNOpsClass(_)) ++
    (1 to 22).map(new TupleNOpsClass(_))

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
    lines.take(s + 1).addString(sb, "", "\n", "\n\n")
    updateOpsClasses(lines.slice(s + 1, e), sb)
    lines.drop(e).addString(sb, "\n\n", "\n", "\n")
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
          case e              => name -> lines.slice(s + 1, e).mkString("", "\n", "\n").unindent
        }
    }.toMap

    classes.iterator.map { c =>
      s"""//// start:${c.name}
         |${oldSources.get(c.name).fold(c.render)(c.updateSource)}
         |//// end:${c.name}
         |""".stripMargin.indent
    }.addString(sb, "\n\n")
  }


  abstract class OpsClass {

    def name: String = tpe.takeWhile(_ != '[')

    def tpe: String

    def self: String = "self"

    def selfTpe: String

    def importSelf: Boolean = false

    def members: Seq[String]

    lazy val render: String =
      s"""implicit class $tpe(private val $self: $selfTpe) extends AnyVal {
         |${(if (importSelf) s"\nimport $self._\n\n" else "").indent}
         |  ////
         |
         |  ////
         |
         |${members.mkString("\n").indent}
         |}
         |""".stripMargin

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
  }


  object ConditionOpsClass extends OpsClass {
    val tpe: String = "ConditionOps"

    val selfTpe: String = "Condition"

    lazy val members: Seq[String] = {
      val not =
        s"""def unary_! : Condition = $self.not()
           |""".stripMargin

      val ops = for {
        (op, m) <- Seq(
          "&&" -> "and",
          "||" -> "or"
        )
        (t, o) <- Seq(
          "Condition" -> "other",
          "Field[java.lang.Boolean]" -> "other",
          "java.lang.Boolean" -> "DSL.inline(other)"
        )
      } yield
        s"""def $op(other: $t): Condition = $self.$m($o)
           |""".stripMargin

      not +: ops
    }
  }


  object FieldOpsClass extends OpsClass {
    val tpe: String = "FieldOps[T]"

    val selfTpe: String = "Field[T]"

    lazy val members: Seq[String] = {
      def condOps(ops: Seq[(String, String)], types: Seq[String]) = for {
        (op, m) <- ops
        t <- types
      } yield
        s"""def $op(other: $t): Condition = $self.$m(other)
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


  object NumberFieldOpsClass extends OpsClass {
    val tpe: String = "NumberFieldOps[T <: Number]"

    val selfTpe: String = "Field[T]"

    lazy val members: Seq[String] = {
      def unary(op: String, body: String) =
        s"""def unary_$op : Field[T] = $body
           |""".stripMargin

      def binOps(ops: Seq[(String, String)], types: Seq[String], body: String => String) = for {
        (op, m) <- ops
        t <- types
      } yield
        s"""def $op(other: $t): Field[T] = ${body(m)}
           |""".stripMargin

      val neg = unary("-", s"$self.neg()")
      val arithmeticOps = neg +: binOps(
        Seq(
          "+" -> "add",
          "-" -> "sub",
          "*" -> "mul",
          "/" -> "div",
          "%" -> "mod"
        ),
        Seq(
          "Number",
          "Field[_ <: Number]"
        ),
        m => s"$self.$m(other)")

      val not = unary("~", s"DSL.bitNot($self)")
      val bitwiseOps = not +: binOps(
        Seq(
          "&" -> "bitAnd",
          "|" -> "bitOr",
          "^" -> "bitXor",
          "~&" -> "bitNand",
          "~|" -> "bitNor",
          "~^" -> "bitXNor",
          "<<" -> "shl",
          ">>" -> "shr"
        ),
        Seq(
          "T",
          "Field[T]"
        ),
        m => s"DSL.$m($self, other)")

      arithmeticOps ++ bitwiseOps
    }
  }


  class RecordNOpsClass(n: Int) extends OpsClass {
    val ts = Util.ts("T", n)

    val tpe: String = s"Record${n}Ops[$ts]"

    val selfTpe: String = s"Record$n[$ts]"

    override val importSelf: Boolean = true

    lazy val members: Seq[String] = {
      val tpe = if (n == 1) "Tuple1[T1]" else s"($ts)"
      val body = if (n == 1) "Tuple1(value1)" else s"(${Util.ns(n, "value" + _)})"
      val toTuple =
        s"""def toTuple: $tpe = $body
           |""".stripMargin

      Seq(toTuple)
    }
  }


  class TupleNOpsClass(n: Int) extends OpsClass {
    val ts = Util.ts("T", n)

    val tpe: String = s"Tuple${n}Ops[$ts]"

    val selfTpe: String = if (n == 1) "Tuple1[T1]" else s"($ts)"

    override val importSelf: Boolean = true

    lazy val members: Seq[String] = {
      val row =
        s"""def row: Row$n[$ts] = DSL.row(${Util.ns(n, "_" + _)})
           |""".stripMargin

      Seq(row)
    }
  }


  object Util {
    def ns(n: Int, f: Int => String): String = (1 to n).map(f).mkString(", ")
    def ts(prefix: String, n: Int): String = ns(n, prefix + _)
  }

  implicit class IndentOps(val self: String) extends AnyVal {
    def indent: String = indent(1)

    def indent(n: Int): String = self.lines.map {
      case ""   => ""
      case line => "  " * n + line
    }.mkString("\n")

    def unindent: String = unindent(1)

    def unindent(n: Int): String =
      if (self.lines.forall {
        case ""   => true
        case line => line.startsWith("  " * n)
      }) self.lines.map(_.drop(n * 2)).mkString("\n")
      else sys.error("cannot unindent")
  }

}
