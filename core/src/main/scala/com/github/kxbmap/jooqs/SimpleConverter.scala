package com.github.kxbmap.jooqs

import scala.reflect.{ClassTag, classTag}

@SerialVersionUID(1L)
abstract class AbstractConverter[T: ClassTag, U: ClassTag]
  extends org.jooq.impl.AbstractConverter(
    classTag[T].runtimeClass.asInstanceOf[Class[T]],
    classTag[U].runtimeClass.asInstanceOf[Class[U]])


@SerialVersionUID(1L)
abstract class SimpleConverter[T >: Null : ClassTag, U >: Null : ClassTag] extends AbstractConverter[T, U] {

  def fromDB(value: T): U

  def toDB(value: U): T

  final def from(databaseObject: T): U = if (databaseObject != null) fromDB(databaseObject) else null

  final def to(userObject: U): T = if (userObject != null) toDB(userObject) else null
}
