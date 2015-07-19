package com.github.kxbmap.jooqs

sealed trait NotNothing[T]

object NotNothing {

  implicit def ambNothing1: NotNothing[Nothing] = sys.error("NotNothing")

  implicit def ambNothing2: NotNothing[Nothing] = sys.error("NotNothing")

  implicit def notNothing[T]: NotNothing[T] = _notNoting.asInstanceOf[NotNothing[T]]

  private[this] final val _notNoting = new NotNothing[Any] {}
}
