package com.github.kxbmap.jooqs

import org.jooq.DSLContext

trait DBSession extends SimpleScope {
  def dsl: DSLContext
}

trait TxDBSession extends DBSession {
  def savepoint[T: TxBoundary](block: => T): T
}

trait UnmanagedDBSession extends DBSession {
  def commit(): Unit

  def rollback(): Unit

  def close(): Unit
}
