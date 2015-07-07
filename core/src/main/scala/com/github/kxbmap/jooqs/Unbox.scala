package com.github.kxbmap.jooqs

sealed trait Unbox[T, U] {
  def unbox(value: T): U
}

object Unbox extends LowPriorityUnboxInstances {

  private def unbox[T, U](f: T => U): Unbox[T, U] = value =>
    if (value == null)
      throw new NullPointerException("value is null. You may use `record.getOpt(field)`")
    else f(value)

  implicit val byteUnbox: Unbox[java.lang.Byte, Byte] = unbox(_.byteValue())
  implicit val shortUnbox: Unbox[java.lang.Short, Short] = unbox(_.shortValue())
  implicit val charUnbox: Unbox[java.lang.Character, Char] = unbox(_.charValue())
  implicit val intUnbox: Unbox[java.lang.Integer, Int] = unbox(_.intValue())
  implicit val longUnbox: Unbox[java.lang.Long, Long] = unbox(_.longValue())
  implicit val floatUnbox: Unbox[java.lang.Float, Float] = unbox(_.floatValue())
  implicit val doubleUnbox: Unbox[java.lang.Double, Double] = unbox(_.doubleValue())
  implicit val booleanUnbox: Unbox[java.lang.Boolean, Boolean] = unbox(_.booleanValue())

}

sealed trait LowPriorityUnboxInstances {

  private[this] final val _noBoxed: Unbox[Any, Any] = value => value

  implicit def noBoxed[T]: Unbox[T, T] = _noBoxed.asInstanceOf[Unbox[T, T]]

}
