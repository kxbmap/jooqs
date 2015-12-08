package jooqs.syntax

import java.sql.Connection
import jooqs._
import jooqs.impl._
import org.jooq._
import org.jooq.impl.DSL
import scala.collection.GenTraversableOnce
import scala.collection.mutable.ArrayBuffer
import scala.util.control.ControlThrowable

object `package` {

  def dsl(implicit session: DBSession): DSLContext =
    session.dsl

  def savepoint[A: TxBoundary](block: => A)(implicit session: TxDBSession): A =
    session.savepoint(block)


  implicit class PrimitiveTypeOps[A](private val self: A) extends AnyVal {
    def box[B](implicit b: Box[A, B]): B =
      b.box(self)
  }

  implicit class PrimitiveTypeOptionOps[A](private val self: Option[A]) extends AnyVal {
    def box[B](implicit b: Box[A, B]): Option[B] =
      self.map(b.box)
  }


  implicit class RecordOps(private val self: Record) extends AnyVal {

    def get[A, B](field: Field[A])(implicit u: Unbox[A, B]): B =
      u.unbox(self.getValue(field))

    def getOpt[A, B](field: Field[A])(implicit u: Unbox[A, B]): Option[B] =
      Option(self.getValue(field)).map(u.unbox)
  }


  implicit class QueryPartOps[A <: QueryPart](private val self: A) extends AnyVal {

    def map[B](f: A => B): B =
      f(self)

    def mapIf[B >: A](cond: Boolean, f: A => B): B =
      if (cond) f(self) else self
  }


  implicit class SelectSelectStepOps[A <: Record](private val self: SelectSelectStep[A]) extends AnyVal {

    def select(fields: Array[Field[_]]): SelectSelectStep[Record] =
      self.select(fields: _*)

    def select(fields: Seq[Field[_]]): SelectSelectStep[Record] =
      self.select(fields: _*)
  }


  implicit class DSLContextOps(private val self: DSLContext) extends AnyVal {

    def select(fields: Array[Field[_]]): SelectSelectStep[Record] =
      self.select(fields: _*)

    def select(fields: Seq[Field[_]]): SelectSelectStep[Record] =
      self.select(fields: _*)

    def withTransaction[A: TxBoundary](body: Configuration => A): A = {
      val ctx = new DefaultTransactionContext(self.configuration.derive())
      val provider = ctx.configuration.transactionProvider()
      provider.begin(ctx)
      val result =
        try body(ctx.configuration) catch {
          case e: ControlThrowable =>
            TxBoundary.commit(provider, ctx)
            throw e

          case e: Throwable =>
            TxBoundary.rollback(e, provider, ctx)
            throw e
        }
      TxBoundary[A].finish(result, provider, ctx)
    }

  }


  implicit class ConnectionProviderOps(private val self: ConnectionProvider) extends AnyVal {

    def withConnection[A](f: Connection => A): A = {
      val c = self.acquire()
      try f(c) finally self.release(c)
    }

  }


  implicit class RecordMapperOps[R <: Record, A](private val self: RecordMapper[R, A]) extends AnyVal {

    def fmap[B](f: A => B): RecordMapper[R, B] =
      r => f(self.map(r))

    def flatMap[B](f: A => RecordMapper[R, B]): RecordMapper[R, B] =
      r => f(self.map(r)).map(r)

    def zip[B](that: RecordMapper[R, B]): RecordMapper[R, (A, B)] =
      zipWith(that)((_, _))

    def zipWith[B, C](that: RecordMapper[R, B])(f: (A, B) => C): RecordMapper[R, C] =
      r => f(self.map(r), that.map(r))

    def unzip[B, C](implicit ev: A =:= (B, C)): (RecordMapper[R, B], RecordMapper[R, C]) =
      (self.map(_)._1, self.map(_)._2)
  }


  implicit class SQLInterpolation(private val sc: StringContext) extends AnyVal {

    def sql(args: Any*): SQL = {
      sc.checkLengths(args)
      val pi = sc.parts.iterator
      val sb = new StringBuilder(pi.next())
      val ab = new ArrayBuffer[Any]()
      var i = 0
      def append(x: Any): Unit = {
        x match {
          case ys: GenTraversableOnce[_] =>
            var first = true
            ys.foreach { y =>
              if (!first) sb ++= ", "
              append(y)
              first = false
            }

          case p: Product =>
            sb += '('
            append(p.productIterator)
            sb += ')'

          case _ =>
            sb += '{'
            sb.append(i)
            sb += '}'
            ab += x
            i += 1
        }
      }
      pi.zip(args.iterator).foreach {
        case (s, a) =>
          append(a)
          sb ++= s
      }
      DSL.sql(sb.result(), ab.asInstanceOf[Seq[AnyRef]]: _*)
    }
  }


  //// generation:start

  //// start:ConditionOps
  implicit class ConditionOps(private val self: Condition) extends AnyVal {

    ////

    ////

    def unary_! : Condition =
      self.not()

    def &&(other: Condition): Condition =
      self.and(other)

    def &&(other: Field[java.lang.Boolean]): Condition =
      self.and(other)

    def &&(other: java.lang.Boolean): Condition =
      self.and(DSL.inline(other))

    def ||(other: Condition): Condition =
      self.or(other)

    def ||(other: Field[java.lang.Boolean]): Condition =
      self.or(other)

    def ||(other: java.lang.Boolean): Condition =
      self.or(DSL.inline(other))
  }

  //// end:ConditionOps

  //// start:FieldOps
  implicit class FieldOps[A](private val self: Field[A]) extends AnyVal {

    ////

    def ||(other: String): Field[String] =
      DSL.concat(self, DSL.value(other))

    def ||(other: Field[_]): Field[String] =
      DSL.concat(self, other)

    ////

    def ===(other: A): Condition =
      self.equal(other)

    def ===(other: Field[A]): Condition =
      self.equal(other)

    def ===(other: Select[_ <: Record1[A]]): Condition =
      self.equal(other)

    def ===(other: QuantifiedSelect[_ <: Record1[A]]): Condition =
      self.equal(other)

    def =!=(other: A): Condition =
      self.notEqual(other)

    def =!=(other: Field[A]): Condition =
      self.notEqual(other)

    def =!=(other: Select[_ <: Record1[A]]): Condition =
      self.notEqual(other)

    def =!=(other: QuantifiedSelect[_ <: Record1[A]]): Condition =
      self.notEqual(other)

    def <>(other: A): Condition =
      self.notEqual(other)

    def <>(other: Field[A]): Condition =
      self.notEqual(other)

    def <>(other: Select[_ <: Record1[A]]): Condition =
      self.notEqual(other)

    def <>(other: QuantifiedSelect[_ <: Record1[A]]): Condition =
      self.notEqual(other)

    def <(other: A): Condition =
      self.lessThan(other)

    def <(other: Field[A]): Condition =
      self.lessThan(other)

    def <(other: Select[_ <: Record1[A]]): Condition =
      self.lessThan(other)

    def <(other: QuantifiedSelect[_ <: Record1[A]]): Condition =
      self.lessThan(other)

    def <=(other: A): Condition =
      self.lessOrEqual(other)

    def <=(other: Field[A]): Condition =
      self.lessOrEqual(other)

    def <=(other: Select[_ <: Record1[A]]): Condition =
      self.lessOrEqual(other)

    def <=(other: QuantifiedSelect[_ <: Record1[A]]): Condition =
      self.lessOrEqual(other)

    def >(other: A): Condition =
      self.greaterThan(other)

    def >(other: Field[A]): Condition =
      self.greaterThan(other)

    def >(other: Select[_ <: Record1[A]]): Condition =
      self.greaterThan(other)

    def >(other: QuantifiedSelect[_ <: Record1[A]]): Condition =
      self.greaterThan(other)

    def >=(other: A): Condition =
      self.greaterOrEqual(other)

    def >=(other: Field[A]): Condition =
      self.greaterOrEqual(other)

    def >=(other: Select[_ <: Record1[A]]): Condition =
      self.greaterOrEqual(other)

    def >=(other: QuantifiedSelect[_ <: Record1[A]]): Condition =
      self.greaterOrEqual(other)

    def <=>(other: A): Condition =
      self.isNotDistinctFrom(other)

    def <=>(other: Field[A]): Condition =
      self.isNotDistinctFrom(other)
  }

  //// end:FieldOps

  //// start:NumberFieldOps
  implicit class NumberFieldOps[A <: Number](private val self: Field[A]) extends AnyVal {

    ////

    ////

    def unary_- : Field[A] =
      self.neg()

    def +(other: Number): Field[A] =
      self.add(other)

    def +(other: Field[_ <: Number]): Field[A] =
      self.add(other)

    def -(other: Number): Field[A] =
      self.sub(other)

    def -(other: Field[_ <: Number]): Field[A] =
      self.sub(other)

    def *(other: Number): Field[A] =
      self.mul(other)

    def *(other: Field[_ <: Number]): Field[A] =
      self.mul(other)

    def /(other: Number): Field[A] =
      self.div(other)

    def /(other: Field[_ <: Number]): Field[A] =
      self.div(other)

    def %(other: Number): Field[A] =
      self.mod(other)

    def %(other: Field[_ <: Number]): Field[A] =
      self.mod(other)

    def unary_~ : Field[A] =
      DSL.bitNot(self)

    def &(other: A): Field[A] =
      DSL.bitAnd(self, other)

    def &(other: Field[A]): Field[A] =
      DSL.bitAnd(self, other)

    def |(other: A): Field[A] =
      DSL.bitOr(self, other)

    def |(other: Field[A]): Field[A] =
      DSL.bitOr(self, other)

    def ^(other: A): Field[A] =
      DSL.bitXor(self, other)

    def ^(other: Field[A]): Field[A] =
      DSL.bitXor(self, other)

    def ~&(other: A): Field[A] =
      DSL.bitNand(self, other)

    def ~&(other: Field[A]): Field[A] =
      DSL.bitNand(self, other)

    def ~|(other: A): Field[A] =
      DSL.bitNor(self, other)

    def ~|(other: Field[A]): Field[A] =
      DSL.bitNor(self, other)

    def ~^(other: A): Field[A] =
      DSL.bitXNor(self, other)

    def ~^(other: Field[A]): Field[A] =
      DSL.bitXNor(self, other)

    def <<(other: A): Field[A] =
      DSL.shl(self, other)

    def <<(other: Field[A]): Field[A] =
      DSL.shl(self, other)

    def >>(other: A): Field[A] =
      DSL.shr(self, other)

    def >>(other: Field[A]): Field[A] =
      DSL.shr(self, other)
  }

  //// end:NumberFieldOps

  //// start:DateTimeFieldOps
  implicit class DateTimeFieldOps[A](private val self: Field[A]) extends AnyVal {

    ////

    ////

    def +[B: IsNumOrInterval](other: B)(implicit A: IsDateTime[A]): Field[A] =
      self.add(DSL.`val`(other))

    def +[B: IsNumOrInterval](other: Field[B])(implicit A: IsDateTime[A]): Field[A] =
      self.add(other)

    def -[B: IsNumOrInterval](other: B)(implicit A: IsDateTime[A]): Field[A] =
      self.sub(DSL.`val`(other))

    def -[B: IsNumOrInterval](other: Field[B])(implicit A: IsDateTime[A]): Field[A] =
      self.sub(other)
  }

  //// end:DateTimeFieldOps

  //// start:Record1Ops
  implicit class Record1Ops[A1](private val self: Record1[A1]) extends AnyVal {

    import self._

    ////

    ////

    def toTuple: Tuple1[A1] =
      Tuple1(value1)
  }

  //// end:Record1Ops

  //// start:Record2Ops
  implicit class Record2Ops[A1, A2](private val self: Record2[A1, A2]) extends AnyVal {

    import self._

    ////

    ////

    def toTuple: (A1, A2) =
      (value1, value2)
  }

  //// end:Record2Ops

  //// start:Record3Ops
  implicit class Record3Ops[A1, A2, A3](private val self: Record3[A1, A2, A3]) extends AnyVal {

    import self._

    ////

    ////

    def toTuple: (A1, A2, A3) =
      (value1, value2, value3)
  }

  //// end:Record3Ops

  //// start:Record4Ops
  implicit class Record4Ops[A1, A2, A3, A4](private val self: Record4[A1, A2, A3, A4]) extends AnyVal {

    import self._

    ////

    ////

    def toTuple: (A1, A2, A3, A4) =
      (value1, value2, value3, value4)
  }

  //// end:Record4Ops

  //// start:Record5Ops
  implicit class Record5Ops[A1, A2, A3, A4, A5](private val self: Record5[A1, A2, A3, A4, A5]) extends AnyVal {

    import self._

    ////

    ////

    def toTuple: (A1, A2, A3, A4, A5) =
      (value1, value2, value3, value4, value5)
  }

  //// end:Record5Ops

  //// start:Record6Ops
  implicit class Record6Ops[A1, A2, A3, A4, A5, A6](private val self: Record6[A1, A2, A3, A4, A5, A6]) extends AnyVal {

    import self._

    ////

    ////

    def toTuple: (A1, A2, A3, A4, A5, A6) =
      (value1, value2, value3, value4, value5, value6)
  }

  //// end:Record6Ops

  //// start:Record7Ops
  implicit class Record7Ops[A1, A2, A3, A4, A5, A6, A7](private val self: Record7[A1, A2, A3, A4, A5, A6, A7]) extends AnyVal {

    import self._

    ////

    ////

    def toTuple: (A1, A2, A3, A4, A5, A6, A7) =
      (value1, value2, value3, value4, value5, value6, value7)
  }

  //// end:Record7Ops

  //// start:Record8Ops
  implicit class Record8Ops[A1, A2, A3, A4, A5, A6, A7, A8](private val self: Record8[A1, A2, A3, A4, A5, A6, A7, A8]) extends AnyVal {

    import self._

    ////

    ////

    def toTuple: (A1, A2, A3, A4, A5, A6, A7, A8) =
      (value1, value2, value3, value4, value5, value6, value7, value8)
  }

  //// end:Record8Ops

  //// start:Record9Ops
  implicit class Record9Ops[A1, A2, A3, A4, A5, A6, A7, A8, A9](private val self: Record9[A1, A2, A3, A4, A5, A6, A7, A8, A9]) extends AnyVal {

    import self._

    ////

    ////

    def toTuple: (A1, A2, A3, A4, A5, A6, A7, A8, A9) =
      (value1, value2, value3, value4, value5, value6, value7, value8, value9)
  }

  //// end:Record9Ops

  //// start:Record10Ops
  implicit class Record10Ops[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10](private val self: Record10[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10]) extends AnyVal {

    import self._

    ////

    ////

    def toTuple: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10) =
      (value1, value2, value3, value4, value5, value6, value7, value8, value9, value10)
  }

  //// end:Record10Ops

  //// start:Record11Ops
  implicit class Record11Ops[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11](private val self: Record11[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11]) extends AnyVal {

    import self._

    ////

    ////

    def toTuple: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11) =
      (value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11)
  }

  //// end:Record11Ops

  //// start:Record12Ops
  implicit class Record12Ops[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12](private val self: Record12[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12]) extends AnyVal {

    import self._

    ////

    ////

    def toTuple: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12) =
      (value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12)
  }

  //// end:Record12Ops

  //// start:Record13Ops
  implicit class Record13Ops[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13](private val self: Record13[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13]) extends AnyVal {

    import self._

    ////

    ////

    def toTuple: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13) =
      (value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12, value13)
  }

  //// end:Record13Ops

  //// start:Record14Ops
  implicit class Record14Ops[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14](private val self: Record14[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14]) extends AnyVal {

    import self._

    ////

    ////

    def toTuple: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14) =
      (value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12, value13, value14)
  }

  //// end:Record14Ops

  //// start:Record15Ops
  implicit class Record15Ops[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15](private val self: Record15[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15]) extends AnyVal {

    import self._

    ////

    ////

    def toTuple: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15) =
      (value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12, value13, value14, value15)
  }

  //// end:Record15Ops

  //// start:Record16Ops
  implicit class Record16Ops[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16](private val self: Record16[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16]) extends AnyVal {

    import self._

    ////

    ////

    def toTuple: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16) =
      (value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12, value13, value14, value15, value16)
  }

  //// end:Record16Ops

  //// start:Record17Ops
  implicit class Record17Ops[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17](private val self: Record17[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17]) extends AnyVal {

    import self._

    ////

    ////

    def toTuple: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17) =
      (value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12, value13, value14, value15, value16, value17)
  }

  //// end:Record17Ops

  //// start:Record18Ops
  implicit class Record18Ops[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18](private val self: Record18[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18]) extends AnyVal {

    import self._

    ////

    ////

    def toTuple: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18) =
      (value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12, value13, value14, value15, value16, value17, value18)
  }

  //// end:Record18Ops

  //// start:Record19Ops
  implicit class Record19Ops[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19](private val self: Record19[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19]) extends AnyVal {

    import self._

    ////

    ////

    def toTuple: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19) =
      (value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12, value13, value14, value15, value16, value17, value18, value19)
  }

  //// end:Record19Ops

  //// start:Record20Ops
  implicit class Record20Ops[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20](private val self: Record20[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20]) extends AnyVal {

    import self._

    ////

    ////

    def toTuple: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20) =
      (value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12, value13, value14, value15, value16, value17, value18, value19, value20)
  }

  //// end:Record20Ops

  //// start:Record21Ops
  implicit class Record21Ops[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21](private val self: Record21[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21]) extends AnyVal {

    import self._

    ////

    ////

    def toTuple: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21) =
      (value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12, value13, value14, value15, value16, value17, value18, value19, value20, value21)
  }

  //// end:Record21Ops

  //// start:Record22Ops
  implicit class Record22Ops[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21, A22](private val self: Record22[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21, A22]) extends AnyVal {

    import self._

    ////

    ////

    def toTuple: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21, A22) =
      (value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12, value13, value14, value15, value16, value17, value18, value19, value20, value21, value22)
  }

  //// end:Record22Ops

  //// start:Tuple1Ops
  implicit class Tuple1Ops[A1](private val self: Tuple1[A1]) extends AnyVal {

    import self._

    ////

    ////

    def row: Row1[A1] =
      DSL.row(_1)
  }

  //// end:Tuple1Ops

  //// start:Tuple2Ops
  implicit class Tuple2Ops[A1, A2](private val self: (A1, A2)) extends AnyVal {

    import self._

    ////

    ////

    def row: Row2[A1, A2] =
      DSL.row(_1, _2)
  }

  //// end:Tuple2Ops

  //// start:Tuple3Ops
  implicit class Tuple3Ops[A1, A2, A3](private val self: (A1, A2, A3)) extends AnyVal {

    import self._

    ////

    ////

    def row: Row3[A1, A2, A3] =
      DSL.row(_1, _2, _3)
  }

  //// end:Tuple3Ops

  //// start:Tuple4Ops
  implicit class Tuple4Ops[A1, A2, A3, A4](private val self: (A1, A2, A3, A4)) extends AnyVal {

    import self._

    ////

    ////

    def row: Row4[A1, A2, A3, A4] =
      DSL.row(_1, _2, _3, _4)
  }

  //// end:Tuple4Ops

  //// start:Tuple5Ops
  implicit class Tuple5Ops[A1, A2, A3, A4, A5](private val self: (A1, A2, A3, A4, A5)) extends AnyVal {

    import self._

    ////

    ////

    def row: Row5[A1, A2, A3, A4, A5] =
      DSL.row(_1, _2, _3, _4, _5)
  }

  //// end:Tuple5Ops

  //// start:Tuple6Ops
  implicit class Tuple6Ops[A1, A2, A3, A4, A5, A6](private val self: (A1, A2, A3, A4, A5, A6)) extends AnyVal {

    import self._

    ////

    ////

    def row: Row6[A1, A2, A3, A4, A5, A6] =
      DSL.row(_1, _2, _3, _4, _5, _6)
  }

  //// end:Tuple6Ops

  //// start:Tuple7Ops
  implicit class Tuple7Ops[A1, A2, A3, A4, A5, A6, A7](private val self: (A1, A2, A3, A4, A5, A6, A7)) extends AnyVal {

    import self._

    ////

    ////

    def row: Row7[A1, A2, A3, A4, A5, A6, A7] =
      DSL.row(_1, _2, _3, _4, _5, _6, _7)
  }

  //// end:Tuple7Ops

  //// start:Tuple8Ops
  implicit class Tuple8Ops[A1, A2, A3, A4, A5, A6, A7, A8](private val self: (A1, A2, A3, A4, A5, A6, A7, A8)) extends AnyVal {

    import self._

    ////

    ////

    def row: Row8[A1, A2, A3, A4, A5, A6, A7, A8] =
      DSL.row(_1, _2, _3, _4, _5, _6, _7, _8)
  }

  //// end:Tuple8Ops

  //// start:Tuple9Ops
  implicit class Tuple9Ops[A1, A2, A3, A4, A5, A6, A7, A8, A9](private val self: (A1, A2, A3, A4, A5, A6, A7, A8, A9)) extends AnyVal {

    import self._

    ////

    ////

    def row: Row9[A1, A2, A3, A4, A5, A6, A7, A8, A9] =
      DSL.row(_1, _2, _3, _4, _5, _6, _7, _8, _9)
  }

  //// end:Tuple9Ops

  //// start:Tuple10Ops
  implicit class Tuple10Ops[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10](private val self: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10)) extends AnyVal {

    import self._

    ////

    ////

    def row: Row10[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10] =
      DSL.row(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10)
  }

  //// end:Tuple10Ops

  //// start:Tuple11Ops
  implicit class Tuple11Ops[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11](private val self: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11)) extends AnyVal {

    import self._

    ////

    ////

    def row: Row11[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11] =
      DSL.row(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11)
  }

  //// end:Tuple11Ops

  //// start:Tuple12Ops
  implicit class Tuple12Ops[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12](private val self: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12)) extends AnyVal {

    import self._

    ////

    ////

    def row: Row12[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12] =
      DSL.row(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12)
  }

  //// end:Tuple12Ops

  //// start:Tuple13Ops
  implicit class Tuple13Ops[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13](private val self: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13)) extends AnyVal {

    import self._

    ////

    ////

    def row: Row13[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13] =
      DSL.row(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13)
  }

  //// end:Tuple13Ops

  //// start:Tuple14Ops
  implicit class Tuple14Ops[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14](private val self: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14)) extends AnyVal {

    import self._

    ////

    ////

    def row: Row14[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14] =
      DSL.row(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14)
  }

  //// end:Tuple14Ops

  //// start:Tuple15Ops
  implicit class Tuple15Ops[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15](private val self: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15)) extends AnyVal {

    import self._

    ////

    ////

    def row: Row15[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15] =
      DSL.row(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15)
  }

  //// end:Tuple15Ops

  //// start:Tuple16Ops
  implicit class Tuple16Ops[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16](private val self: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16)) extends AnyVal {

    import self._

    ////

    ////

    def row: Row16[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16] =
      DSL.row(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16)
  }

  //// end:Tuple16Ops

  //// start:Tuple17Ops
  implicit class Tuple17Ops[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17](private val self: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17)) extends AnyVal {

    import self._

    ////

    ////

    def row: Row17[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17] =
      DSL.row(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17)
  }

  //// end:Tuple17Ops

  //// start:Tuple18Ops
  implicit class Tuple18Ops[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18](private val self: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18)) extends AnyVal {

    import self._

    ////

    ////

    def row: Row18[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18] =
      DSL.row(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18)
  }

  //// end:Tuple18Ops

  //// start:Tuple19Ops
  implicit class Tuple19Ops[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19](private val self: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19)) extends AnyVal {

    import self._

    ////

    ////

    def row: Row19[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19] =
      DSL.row(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19)
  }

  //// end:Tuple19Ops

  //// start:Tuple20Ops
  implicit class Tuple20Ops[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20](private val self: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20)) extends AnyVal {

    import self._

    ////

    ////

    def row: Row20[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20] =
      DSL.row(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20)
  }

  //// end:Tuple20Ops

  //// start:Tuple21Ops
  implicit class Tuple21Ops[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21](private val self: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21)) extends AnyVal {

    import self._

    ////

    ////

    def row: Row21[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21] =
      DSL.row(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21)
  }

  //// end:Tuple21Ops

  //// start:Tuple22Ops
  implicit class Tuple22Ops[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21, A22](private val self: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21, A22)) extends AnyVal {

    import self._

    ////

    ////

    def row: Row22[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21, A22] =
      DSL.row(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22)
  }

  //// end:Tuple22Ops

  //// generation:end
}
