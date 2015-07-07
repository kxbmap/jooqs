package com.github.kxbmap.jooqs.db

import org.jooq.{Configuration, Transaction, TransactionContext}

class DefaultTransactionContext(val configuration: Configuration) extends TransactionContext with Scope {

  override lazy val data: java.util.Map[AnyRef, AnyRef] = new java.util.HashMap()

  private[this] var tx: Transaction = _
  private[this] var e: Exception = _

  def transaction(): Transaction = tx

  def transaction(transaction: Transaction): TransactionContext = {
    tx = transaction
    this
  }

  def cause(): Exception = e

  def cause(cause: Exception): TransactionContext = {
    e = cause
    this
  }

}
