package jooqs.impl

import jooqs.Box
import org.jooq.types.Interval

sealed trait IsNumOrInterval[-A]

private[impl] sealed abstract class IsNumOrInterval0 {

  implicit object number extends IsNumOrInterval[Number]

}

object IsNumOrInterval extends IsNumOrInterval0 {

  implicit object interval extends IsNumOrInterval[Interval]

  private[this] val any: IsNumOrInterval[Any] = new IsNumOrInterval[Any] {}

  implicit def primitiveNumber[A, B <: Number](implicit b: Box[A, B]): IsNumOrInterval[A] =
    any.asInstanceOf[IsNumOrInterval[A]]

}
