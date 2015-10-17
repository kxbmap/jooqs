package jooqs

import scala.language.implicitConversions

trait DisableAutoboxing {

  implicit def ambByte2Byte(x: Byte): java.lang.Byte = sys.error("disabled")

  implicit def ambShort2Short(x: Short): java.lang.Short = sys.error("disabled")

  implicit def ambChar2Character(x: Char): java.lang.Character = sys.error("disabled")

  implicit def ambInt2Integer(x: Int): java.lang.Integer = sys.error("disabled")

  implicit def ambLong2Long(x: Long): java.lang.Long = sys.error("disabled")

  implicit def ambFloat2Float(x: Float): java.lang.Float = sys.error("disabled")

  implicit def ambDouble2Double(x: Double): java.lang.Double = sys.error("disabled")

  implicit def ambBoolean2Boolean(x: Boolean): java.lang.Boolean = sys.error("disabled")

}
