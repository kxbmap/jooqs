package jooqs

sealed trait Box[T, U] {
  def box(value: T): U
}

object Box {

  implicit val byteBox: Box[Byte, java.lang.Byte] = Byte.box
  implicit val shortBox: Box[Short, java.lang.Short] = Short.box
  implicit val charBox: Box[Char, java.lang.Character] = Char.box
  implicit val intBox: Box[Int, java.lang.Integer] = Int.box
  implicit val longBox: Box[Long, java.lang.Long] = Long.box
  implicit val floatBox: Box[Float, java.lang.Float] = Float.box
  implicit val doubleBox: Box[Double, java.lang.Double] = Double.box
  implicit val booleanBox: Box[Boolean, java.lang.Boolean] = Boolean.box

}
