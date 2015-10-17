package jooqs

import scala.language.implicitConversions

trait DisableAutoUnboxing {

  implicit def ambByte2byte(x: java.lang.Byte): Byte = sys.error("disabled")

  implicit def ambShort2short(x: java.lang.Short): Short = sys.error("disabled")

  implicit def ambCharacter2char(x: java.lang.Character): Char = sys.error("disabled")

  implicit def ambInteger2int(x: java.lang.Integer): Int = sys.error("disabled")

  implicit def ambLong2long(v: java.lang.Long): Long = sys.error("disabled")

  implicit def ambFloat2float(x: java.lang.Float): Float = sys.error("disabled")

  implicit def ambDouble2double(x: java.lang.Double): Double = sys.error("disabled")

  implicit def ambBoolean2boolean(x: java.lang.Boolean): Boolean = sys.error("disabled")

}
