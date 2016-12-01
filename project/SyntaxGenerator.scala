import sbt.Keys._
import sbt._
import scala.util.Try

object SyntaxGenerator extends AutoPlugin {

  override def requires: Plugins = plugins.JvmPlugin

  object autoImport {

    val generateSyntax = taskKey[Unit]("generate syntax object")

  }

  import autoImport._

  override lazy val projectSettings: Seq[Setting[_]] = Seq(
    generateSyntax := {
      gen((scalaSource in Compile).value, streams.value.log)
    },
    compile in Compile := (compile in Compile).dependsOn(generateSyntax).value
  )


  val pkg = "jooqs.syntax"

  lazy val classes = Seq(
    ConditionOpsClass,
    FieldOpsClass,
    NumberFieldOpsClass,
    DateTimeFieldOpsClass
  ) ++
    (1 to 22).map(new RecordNOpsClass(_)) ++
    (1 to 22).map(new TupleNOpsClass(_))

  def gen(scalaSource: File, log: Logger): Unit = {
    val file = pkg.split('.').foldLeft(scalaSource)(_ / _) / "package.scala"
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
       |import jooqs.impl._
       |import org.jooq._
       |import org.jooq.impl.DSL
       |
       |object `package` {
       |
       |  //// generation:start
       |
       |${classes.map(_.render).mkString("\n\n").indent}
       |
       |  //// generation:end
       |}
       |""".stripMargin


  def updateSource(old: String): String = {
    val lines = old.linesIterator.toVector
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
      case (start(n), i) => n -> i
    }
    val oldSources = (starts :+ ("sentinel", lines.size)).sliding(2).collect {
      case Vector((n, s), (_, next)) =>
        lines.indexWhere(_.matches(end(n)), s) match {
          case -1             => sys.error(s"missing end marker: $n start: $s")
          case e if e >= next => sys.error(s"invalid end marker position: $n start: $s, end: $e, next: $next")
          case e              => n -> lines.slice(s + 1, e).mkString("", "\n", "\n").unindent
        }
    }.toMap

    classes.iterator.map { c =>
      s"""//// start:${c.tpe.name}
         |${oldSources.get(c.tpe.name).fold(c.render)(c.updateSource)}
         |//// end:${c.tpe.name}
         |""".stripMargin.indent
    }.addString(sb, "\n\n")
  }


  abstract class OpsClass {

    def tpe: Type

    def self: Arg

    def importSelf: Boolean = false

    def methods: Seq[Method]

    def render: String =
      s"""implicit class ${tpe.render}(private val ${self.render}) extends AnyVal {
         |${(if (importSelf) s"\nimport ${self.name}._\n\n" else "").indent}
         |  ////
         |
         |  ////
         |
         |${methods.map(_.render).mkString("\n").indent}
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


  case class Type(name: String, typeArgs: Seq[Type] = Nil, contextBounds: Seq[Type] = Nil, upperBound: Option[Type] = None) {

    require(tupleArity.forall(_ == typeArgs.length))

    def apply(ts: Type*): Type = copy(typeArgs = ts)

    def ::(tpe: Type): Type = tpe.copy(contextBounds = tpe.contextBounds :+ this)

    def <::(tpe: Type): Type = tpe.copy(upperBound = Some(this))

    def render: String = {
      val tas = typeArgs.map(_.render)
      val s = tupleArity match {
        case Some(n) if n >= 2 => tas.mkString("(", ", ", ")")
        case _                 => s"$name${if (tas.isEmpty) "" else tas.mkString("[", ", ", "]")}"
      }
      val ctx = if (contextBounds.isEmpty) "" else contextBounds.map(_.render).mkString(": ", " : ", "")
      val upper = upperBound.fold("")(u => s" <: ${u.render}")
      s"$s$ctx$upper"
    }

    private def tupleArity: Option[Int] =
      name.splitAt(5) match {
        case ("Tuple", s) => Try(s.toInt).filter(n => n >= 1 && n <= 22).toOption
        case _            => None
      }
  }

  object Type {

    def apply(name: String, typeArgs: Type*): Type = Type(name, typeArgs)

    def Tuple(n: Int, prefix: String): Type = Type(s"Tuple$n", (1 to n).map(i => Type(s"$prefix$i")))

    val __ = Type("_")

    val Number = Type("Number")
    val JBoolean = Type("java.lang.Boolean")

    val Field = Type("Field")
    val Record1 = Type("Record1")
    val Condition = Type("Condition")
    val Select = Type("Select")
    val QuantifiedSelect = Type("QuantifiedSelect")

    val IsDateTime = Type("IsDateTime")
    val IsNumOrInterval = Type("IsNumOrInterval")
  }

  case class Arg(name: String, tpe: Type, implicits: Boolean = false) {
    def render: String = s"$name: ${tpe.render}"
  }

  case class Method(name: String, tpe: Type, argLists: Seq[Seq[Arg]], body: String, typeArgs: Seq[Type] = Nil) {
    def render: String = {
      val tas = if (typeArgs.isEmpty) "" else typeArgs.map(_.render).mkString("[", ", ", "]")
      val args = argLists.map { xs =>
        if (xs.isEmpty) ""
        else {
          val s = if (xs.exists(_.implicits)) "(implicit " else "("
          xs.map(_.render).mkString(s, ", ", ")")
        }
      }.mkString
      s"""def $name$tas$args: ${tpe.render} =
         |  $body
         |""".stripMargin
    }
  }


  import Type._

  object ConditionOpsClass extends OpsClass {

    def tpe: Type = Type("ConditionOps")

    def self: Arg = Arg("self", Condition)

    def methods: Seq[Method] = {
      val not = Method("unary_! ", Condition, Nil, s"${self.name}.not()")
      val ops = for {
        (op, m) <- Seq(
          "&&" -> "and",
          "||" -> "or"
        )
        a = "other"
        (t, o) <- Seq(
          Condition -> a,
          Field(JBoolean) -> a,
          JBoolean -> s"DSL.inline($a)"
        )
      } yield Method(op, Condition, Seq(Seq(Arg(a, t))), s"${self.name}.$m($o)")
      not +: ops
    }
  }


  object FieldOpsClass extends OpsClass {
    val A = Type("A")

    def tpe: Type = Type("FieldOps", A)

    def self: Arg = Arg("self", Field(A))

    def methods: Seq[Method] = {
      def condOps(ops: Seq[(String, String)], types: Seq[Type]) =
        for {
          (op, m) <- ops
          t <- types
          a = Arg("other", t)
        } yield Method(op, Condition, Seq(Seq(a)), s"${self.name}.$m(${a.name})")

      val standardCondOps = condOps(
        Seq(
          "===" -> "equal",
          "=!=" -> "notEqual",
          "<>"  -> "notEqual",
          "<"   -> "lessThan",
          "<="  -> "lessOrEqual",
          ">"   -> "greaterThan",
          ">="  -> "greaterOrEqual"
        ), Seq(
          A,
          Field(A),
          Select(__ <:: Record1(A)),
          QuantifiedSelect(__ <:: Record1(A))
        ))

      val distinctCondOps = condOps(
        Seq(
          "<=>" -> "isNotDistinctFrom"
        ),
        Seq(
          A,
          Field(A)
        ))

      standardCondOps ++ distinctCondOps
    }
  }


  object NumberFieldOpsClass extends OpsClass {
    val A = Type("A")

    def tpe: Type = Type("NumberFieldOps", A <:: Number)

    def self: Arg = Arg("self", Field(A))

    def methods: Seq[Method] = {
      def unary(op: String, body: String) =
        Method(s"unary_$op ", Field(A), Nil, body)

      def binOps(ops: Seq[(String, String)], types: Seq[Type], body: (String, Arg) => String) =
        for {
          (op, m) <- ops
          t <- types
          a = Arg("other", t)
        } yield Method(op, Field(A), Seq(Seq(a)), body(m, a))

      val neg = unary("-", s"${self.name}.neg()")
      val arithmeticOps = neg +: binOps(
        Seq(
          "+" -> "add",
          "-" -> "sub",
          "*" -> "mul",
          "/" -> "div",
          "%" -> "mod"
        ),
        Seq(
          Number,
          Field(__ <:: Number)
        ),
        (m, a) => s"${self.name}.$m(${a.name})")

      val not = unary("~", s"DSL.bitNot(${self.name})")
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
          A,
          Field(A)
        ),
        (m, a) => s"DSL.$m(${self.name}, ${a.name})")

      arithmeticOps ++ bitwiseOps
    }
  }


  object DateTimeFieldOpsClass extends OpsClass {
    val A = Type("A")
    val B = Type("B")

    def tpe: Type = Type("DateTimeFieldOps", A)

    def self: Arg = Arg("self", Field(A))

    def methods: Seq[Method] =
      for {
        (op, m) <- Seq(
          "+" -> "add",
          "-" -> "sub"
        )
        (t, f) <- Seq(
          B -> ((a: Arg) => s"DSL.`val`(${a.name})"),
          Field(B) -> ((_: Arg).name)
        )
        a = Arg("other", t)
        ev = Arg("A", IsDateTime(A), implicits = true)
      } yield Method(op, Field(A), Seq(Seq(a), Seq(ev)), s"${self.name}.$m(${f(a)})", Seq(B :: IsNumOrInterval))
  }


  class RecordNOpsClass(n: Int) extends OpsClass {
    val TA = Tuple(n, "A")

    def tpe: Type = Type(s"Record${n}Ops", TA.typeArgs)

    def self: Arg = Arg("self", Type(s"Record$n", TA.typeArgs))

    override val importSelf: Boolean = true

    def methods: Seq[Method] = Seq(
      Method("toTuple", TA, Nil, if (n == 1) "Tuple1(value1)" else s"(${Util.ns(n, "value" + _)})")
    )
  }


  class TupleNOpsClass(n: Int) extends OpsClass {
    val TA = Tuple(n, "A")

    def tpe: Type = Type(s"Tuple${n}Ops", TA.typeArgs)

    def self: Arg = Arg("self", TA)

    override val importSelf: Boolean = true

    def methods: Seq[Method] = Seq(
      Method("row", Type(s"Row$n", TA.typeArgs), Nil, s"DSL.row(${Util.ns(n, "_" + _)})")
    )
  }


  object Util {
    def ns(n: Int, f: Int => String): String = (1 to n).map(f).mkString(", ")
  }

  implicit class IndentOps(val self: String) extends AnyVal {
    def indent: String = indent(1)

    def indent(n: Int): String = self.linesIterator.map {
      case ""   => ""
      case line => "  " * n + line
    }.mkString("\n")

    def unindent: String = unindent(1)

    def unindent(n: Int): String =
      if (self.linesIterator.forall {
        case ""   => true
        case line => line.startsWith("  " * n)
      }) self.linesIterator.map(_.drop(n * 2)).mkString("\n")
      else sys.error("cannot unindent")
  }

}
