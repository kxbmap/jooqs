package com.github.kxbmap.jooqs

import org.jooq.TransactionContext

object `package` {

  private[jooqs] implicit class TransactionContextOps(private val self: TransactionContext) extends AnyVal {

    def cause(cause: Throwable): TransactionContext = cause match {
      case e: Exception => self.cause(e)
      case _            => self
    }
  }

}
