package jooqs

sealed trait Box[T, U] {
  def box(value: T): U
}

object Box {

  private def box[T, U](f: T => U): Box[T, U] =
    new Box[T, U] {
      def box(value: T): U = f(value)
    }

  implicit val byteBox: Box[Byte, java.lang.Byte] = box(Byte.box)
  implicit val shortBox: Box[Short, java.lang.Short] = box(Short.box)
  implicit val charBox: Box[Char, java.lang.Character] = box(Char.box)
  implicit val intBox: Box[Int, java.lang.Integer] = box(Int.box)
  implicit val longBox: Box[Long, java.lang.Long] = box(Long.box)
  implicit val floatBox: Box[Float, java.lang.Float] = box(Float.box)
  implicit val doubleBox: Box[Double, java.lang.Double] = box(Double.box)
  implicit val booleanBox: Box[Boolean, java.lang.Boolean] = box(Boolean.box)

}
