package com.github.kxbmap.jooqs.db

import java.sql.Connection
import org.jooq.{Configuration, DSLContext}
import scala.language.implicitConversions
import scala.util.DynamicVariable

trait DBSession extends Scope {
  def dslContext: DSLContext
}

trait TxDBSession extends DBSession {
  def savepoint[T](block: => T): T
}

trait UnmanagedDBSession extends DBSession {
  def commit(): Unit

  def rollback(): Unit

  def close(): Unit
}

private[db] class DefaultTxDBSession(top: Configuration) extends TxDBSession {
  private val configVar = new DynamicVariable(top)

  def configuration: Configuration = configVar.value

  def dslContext: DSLContext = new ScalaDSLContext(configuration)

  def savepoint[T](block: => T): T = dslContext.transactionResult(configVar.withValue(_)(block))
}

private[db] class DefaultUnmanagedDBSession(connection: Connection, c: Configuration) extends UnmanagedDBSession {

  lazy val configuration: Configuration = c.derive(connection)

  lazy val dslContext: DSLContext = new ScalaDSLContext(configuration)

  def commit(): Unit = connection.commit()

  def rollback(): Unit = connection.rollback()

  def close(): Unit = connection.close()
}
