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

  //// generation:end
}
