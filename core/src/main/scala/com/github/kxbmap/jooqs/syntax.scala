package com.github.kxbmap.jooqs

import com.github.kxbmap.jooqs.db.DBSession
import org.jooq._
import org.jooq.impl.DSL

object syntax {

  def dsl(implicit session: DBSession): DSLContext = session.dslContext


  implicit class PrimitiveTypeOps[T](private val self: T) extends AnyVal {
    def boxed[U](implicit b: Box[T, U]): U = b.box(self)
  }

  implicit class PrimitiveTypeOptionOps[T](private val self: Option[T]) extends AnyVal {
    def boxed[U](implicit b: Box[T, U]): Option[U] = self.map(b.box)
  }


  implicit class RecordOps(private val self: Record) extends AnyVal {

    def get[T, U](field: Field[T])(implicit u: Unbox[T, U]): U = u.unbox(self.getValue(field))

    def getOpt[T, U](field: Field[T])(implicit u: Unbox[T, U]): Option[U] = Option(self.getValue(field)).map(u.unbox)
  }


  implicit class QueryPartOps[Q <: QueryPart](private val self: Q) extends AnyVal {

    def map[R](f: Q => R): R = f(self)

    def mapIf[R >: Q](cond: Boolean, f: Q => R): R = if (cond) f(self) else self
  }


  implicit class SelectSelectStepOps[R <: Record](private val self: SelectSelectStep[R]) extends AnyVal {

    def select(fields: Array[Field[_]]): SelectSelectStep[Record] = self.select(fields: _*)

    def select(fields: Seq[Field[_]]): SelectSelectStep[Record] = self.select(fields: _*)
  }


  implicit class DSLContextOps(private val self: DSLContext) extends AnyVal {

    def select(fields: Array[Field[_]]): SelectSelectStep[Record] = self.select(fields: _*)

    def select(fields: Seq[Field[_]]): SelectSelectStep[Record] = self.select(fields: _*)
  }

  //// generation:start

  //// start:FieldOps
  implicit class FieldOps[T](private val self: Field[T]) extends AnyVal {

    ////

    def ||(other: String): Field[String] = DSL.concat(self, DSL.value(other))

    def ||(other: Field[_]): Field[String] = DSL.concat(self, other)

    ////

    def ===(other: T): Condition = self.equal(other)

    def ===(other: Field[T]): Condition = self.equal(other)

    def ===(other: Select[_ <: Record1[T]]): Condition = self.equal(other)

    def ===(other: QuantifiedSelect[_ <: Record1[T]]): Condition = self.equal(other)

    def =!=(other: T): Condition = self.notEqual(other)

    def =!=(other: Field[T]): Condition = self.notEqual(other)

    def =!=(other: Select[_ <: Record1[T]]): Condition = self.notEqual(other)

    def =!=(other: QuantifiedSelect[_ <: Record1[T]]): Condition = self.notEqual(other)

    def <>(other: T): Condition = self.notEqual(other)

    def <>(other: Field[T]): Condition = self.notEqual(other)

    def <>(other: Select[_ <: Record1[T]]): Condition = self.notEqual(other)

    def <>(other: QuantifiedSelect[_ <: Record1[T]]): Condition = self.notEqual(other)

    def <(other: T): Condition = self.lessThan(other)

    def <(other: Field[T]): Condition = self.lessThan(other)

    def <(other: Select[_ <: Record1[T]]): Condition = self.lessThan(other)

    def <(other: QuantifiedSelect[_ <: Record1[T]]): Condition = self.lessThan(other)

    def <=(other: T): Condition = self.lessOrEqual(other)

    def <=(other: Field[T]): Condition = self.lessOrEqual(other)

    def <=(other: Select[_ <: Record1[T]]): Condition = self.lessOrEqual(other)

    def <=(other: QuantifiedSelect[_ <: Record1[T]]): Condition = self.lessOrEqual(other)

    def >(other: T): Condition = self.greaterThan(other)

    def >(other: Field[T]): Condition = self.greaterThan(other)

    def >(other: Select[_ <: Record1[T]]): Condition = self.greaterThan(other)

    def >(other: QuantifiedSelect[_ <: Record1[T]]): Condition = self.greaterThan(other)

    def >=(other: T): Condition = self.greaterOrEqual(other)

    def >=(other: Field[T]): Condition = self.greaterOrEqual(other)

    def >=(other: Select[_ <: Record1[T]]): Condition = self.greaterOrEqual(other)

    def >=(other: QuantifiedSelect[_ <: Record1[T]]): Condition = self.greaterOrEqual(other)

    def <=>(other: T): Condition = self.isNotDistinctFrom(other)

    def <=>(other: Field[T]): Condition = self.isNotDistinctFrom(other)
  }

  //// end:FieldOps

  //// start:NumberFieldOps
  implicit class NumberFieldOps[T <: Number](private val self: Field[T]) extends AnyVal {

    ////

    ////

    def unary_- : Field[T] = self.neg()

    def +(other: Number): Field[T] = self.add(other)

    def +(other: Field[_ <: Number]): Field[T] = self.add(other)

    def -(other: Number): Field[T] = self.sub(other)

    def -(other: Field[_ <: Number]): Field[T] = self.sub(other)

    def *(other: Number): Field[T] = self.mul(other)

    def *(other: Field[_ <: Number]): Field[T] = self.mul(other)

    def /(other: Number): Field[T] = self.div(other)

    def /(other: Field[_ <: Number]): Field[T] = self.div(other)

    def %(other: Number): Field[T] = self.mod(other)

    def %(other: Field[_ <: Number]): Field[T] = self.mod(other)

    def unary_~ : Field[T] = DSL.bitNot(self)

    def &(other: T): Field[T] = DSL.bitAnd(self, other)

    def &(other: Field[T]): Field[T] = DSL.bitAnd(self, other)

    def |(other: T): Field[T] = DSL.bitOr(self, other)

    def |(other: Field[T]): Field[T] = DSL.bitOr(self, other)

    def ^(other: T): Field[T] = DSL.bitXor(self, other)

    def ^(other: Field[T]): Field[T] = DSL.bitXor(self, other)

    def ~&(other: T): Field[T] = DSL.bitNand(self, other)

    def ~&(other: Field[T]): Field[T] = DSL.bitNand(self, other)

    def ~|(other: T): Field[T] = DSL.bitNor(self, other)

    def ~|(other: Field[T]): Field[T] = DSL.bitNor(self, other)

    def ~^(other: T): Field[T] = DSL.bitXNor(self, other)

    def ~^(other: Field[T]): Field[T] = DSL.bitXNor(self, other)

    def <<(other: T): Field[T] = DSL.shl(self, other)

    def <<(other: Field[T]): Field[T] = DSL.shl(self, other)

    def >>(other: T): Field[T] = DSL.shr(self, other)

    def >>(other: Field[T]): Field[T] = DSL.shr(self, other)
  }

  //// end:NumberFieldOps

  //// start:Record1Ops
  implicit class Record1Ops[T1](private val self: Record1[T1]) extends AnyVal {

    import self._

    ////

    ////

    def asTuple: Tuple1[T1] = Tuple1(value1)
  }

  //// end:Record1Ops

  //// start:Record2Ops
  implicit class Record2Ops[T1, T2](private val self: Record2[T1, T2]) extends AnyVal {

    import self._

    ////

    ////

    def asTuple: (T1, T2) = (value1, value2)
  }

  //// end:Record2Ops

  //// start:Record3Ops
  implicit class Record3Ops[T1, T2, T3](private val self: Record3[T1, T2, T3]) extends AnyVal {

    import self._

    ////

    ////

    def asTuple: (T1, T2, T3) = (value1, value2, value3)
  }

  //// end:Record3Ops

  //// start:Record4Ops
  implicit class Record4Ops[T1, T2, T3, T4](private val self: Record4[T1, T2, T3, T4]) extends AnyVal {

    import self._

    ////

    ////

    def asTuple: (T1, T2, T3, T4) = (value1, value2, value3, value4)
  }

  //// end:Record4Ops

  //// start:Record5Ops
  implicit class Record5Ops[T1, T2, T3, T4, T5](private val self: Record5[T1, T2, T3, T4, T5]) extends AnyVal {

    import self._

    ////

    ////

    def asTuple: (T1, T2, T3, T4, T5) = (value1, value2, value3, value4, value5)
  }

  //// end:Record5Ops

  //// start:Record6Ops
  implicit class Record6Ops[T1, T2, T3, T4, T5, T6](private val self: Record6[T1, T2, T3, T4, T5, T6]) extends AnyVal {

    import self._

    ////

    ////

    def asTuple: (T1, T2, T3, T4, T5, T6) = (value1, value2, value3, value4, value5, value6)
  }

  //// end:Record6Ops

  //// start:Record7Ops
  implicit class Record7Ops[T1, T2, T3, T4, T5, T6, T7](private val self: Record7[T1, T2, T3, T4, T5, T6, T7]) extends AnyVal {

    import self._

    ////

    ////

    def asTuple: (T1, T2, T3, T4, T5, T6, T7) = (value1, value2, value3, value4, value5, value6, value7)
  }

  //// end:Record7Ops

  //// start:Record8Ops
  implicit class Record8Ops[T1, T2, T3, T4, T5, T6, T7, T8](private val self: Record8[T1, T2, T3, T4, T5, T6, T7, T8]) extends AnyVal {

    import self._

    ////

    ////

    def asTuple: (T1, T2, T3, T4, T5, T6, T7, T8) = (value1, value2, value3, value4, value5, value6, value7, value8)
  }

  //// end:Record8Ops

  //// start:Record9Ops
  implicit class Record9Ops[T1, T2, T3, T4, T5, T6, T7, T8, T9](private val self: Record9[T1, T2, T3, T4, T5, T6, T7, T8, T9]) extends AnyVal {

    import self._

    ////

    ////

    def asTuple: (T1, T2, T3, T4, T5, T6, T7, T8, T9) = (value1, value2, value3, value4, value5, value6, value7, value8, value9)
  }

  //// end:Record9Ops

  //// start:Record10Ops
  implicit class Record10Ops[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10](private val self: Record10[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10]) extends AnyVal {

    import self._

    ////

    ////

    def asTuple: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) = (value1, value2, value3, value4, value5, value6, value7, value8, value9, value10)
  }

  //// end:Record10Ops

  //// start:Record11Ops
  implicit class Record11Ops[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11](private val self: Record11[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11]) extends AnyVal {

    import self._

    ////

    ////

    def asTuple: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11) = (value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11)
  }

  //// end:Record11Ops

  //// start:Record12Ops
  implicit class Record12Ops[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12](private val self: Record12[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12]) extends AnyVal {

    import self._

    ////

    ////

    def asTuple: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12) = (value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12)
  }

  //// end:Record12Ops

  //// start:Record13Ops
  implicit class Record13Ops[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13](private val self: Record13[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13]) extends AnyVal {

    import self._

    ////

    ////

    def asTuple: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13) = (value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12, value13)
  }

  //// end:Record13Ops

  //// start:Record14Ops
  implicit class Record14Ops[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14](private val self: Record14[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14]) extends AnyVal {

    import self._

    ////

    ////

    def asTuple: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14) = (value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12, value13, value14)
  }

  //// end:Record14Ops

  //// start:Record15Ops
  implicit class Record15Ops[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15](private val self: Record15[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15]) extends AnyVal {

    import self._

    ////

    ////

    def asTuple: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15) = (value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12, value13, value14, value15)
  }

  //// end:Record15Ops

  //// start:Record16Ops
  implicit class Record16Ops[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16](private val self: Record16[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16]) extends AnyVal {

    import self._

    ////

    ////

    def asTuple: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16) = (value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12, value13, value14, value15, value16)
  }

  //// end:Record16Ops

  //// start:Record17Ops
  implicit class Record17Ops[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17](private val self: Record17[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17]) extends AnyVal {

    import self._

    ////

    ////

    def asTuple: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17) = (value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12, value13, value14, value15, value16, value17)
  }

  //// end:Record17Ops

  //// start:Record18Ops
  implicit class Record18Ops[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18](private val self: Record18[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18]) extends AnyVal {

    import self._

    ////

    ////

    def asTuple: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18) = (value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12, value13, value14, value15, value16, value17, value18)
  }

  //// end:Record18Ops

  //// start:Record19Ops
  implicit class Record19Ops[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19](private val self: Record19[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19]) extends AnyVal {

    import self._

    ////

    ////

    def asTuple: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19) = (value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12, value13, value14, value15, value16, value17, value18, value19)
  }

  //// end:Record19Ops

  //// start:Record20Ops
  implicit class Record20Ops[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20](private val self: Record20[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20]) extends AnyVal {

    import self._

    ////

    ////

    def asTuple: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20) = (value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12, value13, value14, value15, value16, value17, value18, value19, value20)
  }

  //// end:Record20Ops

  //// start:Record21Ops
  implicit class Record21Ops[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21](private val self: Record21[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21]) extends AnyVal {

    import self._

    ////

    ////

    def asTuple: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21) = (value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12, value13, value14, value15, value16, value17, value18, value19, value20, value21)
  }

  //// end:Record21Ops

  //// start:Record22Ops
  implicit class Record22Ops[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22](private val self: Record22[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22]) extends AnyVal {

    import self._

    ////

    ////

    def asTuple: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22) = (value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12, value13, value14, value15, value16, value17, value18, value19, value20, value21, value22)
  }

  //// end:Record22Ops

  //// generation:end
}
