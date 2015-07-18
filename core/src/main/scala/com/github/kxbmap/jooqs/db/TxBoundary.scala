package com.github.kxbmap.jooqs.db

import org.jooq.{TransactionContext, TransactionProvider}
import scala.util.{Failure, Success, Try}

trait TxBoundary[T] {
  def finish(result: T, provider: TransactionProvider, ctx: TransactionContext): Unit
}

object TxBoundary {

  final val Key = "com.github.kxbmap.jooqs.db.TxBoundary"

  def apply[T](implicit b: TxBoundary[T]): TxBoundary[T] = b


  private[this] final val _defaultTxBoundary: TxBoundary[Any] = (_, provider, ctx) => provider.commit(ctx)

  implicit def defaultTxBoundary[T]: TxBoundary[T] = _defaultTxBoundary.asInstanceOf[TxBoundary[T]]


  private[this] final val _tryTxBoundary: TxBoundary[Try[Any]] = (result, provider, ctx) => {
    result match {
      case Success(_)            => provider.commit(ctx)
      case Failure(e: Exception) => provider.rollback(ctx.cause(e))
      case Failure(_)            => provider.rollback(ctx)
    }
  }

  implicit def tryTxBoundary[T]: TxBoundary[Try[T]] = _tryTxBoundary.asInstanceOf[TxBoundary[Try[T]]]

}
