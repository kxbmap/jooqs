package jooqs

import org.jooq.DSLContext

trait DBSession extends SimpleScope {
  protected[jooqs] def dsl: DSLContext
}

trait TxDBSession extends DBSession {
  protected[jooqs] def savepoint[T: TxBoundary](block: => T): T
}

trait UnmanagedDBSession extends DBSession {
  def commit(): Unit

  def rollback(): Unit

  def close(): Unit
}
