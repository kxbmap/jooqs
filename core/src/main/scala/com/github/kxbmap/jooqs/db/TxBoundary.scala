package com.github.kxbmap.jooqs.db

import com.github.kxbmap.jooqs.syntax._
import org.jooq.{TransactionContext, TransactionProvider}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

trait TxBoundary[T] {
  def finish(result: T, provider: TransactionProvider, ctx: TransactionContext): T
}

object TxBoundary {

  final val Key = "com.github.kxbmap.jooqs.db.TxBoundary"

  def apply[T](implicit b: TxBoundary[T]): TxBoundary[T] = b


  implicit def exceptionTxBoundary[T]: TxBoundary[T] = _exceptionTxBoundary.asInstanceOf[TxBoundary[T]]

  private[this] final val _exceptionTxBoundary: TxBoundary[Any] = (result, provider, ctx) => {
    provider.commit(ctx)
    result
  }


  implicit def tryTxBoundary[T]: TxBoundary[Try[T]] = _tryTxBoundary.asInstanceOf[TxBoundary[Try[T]]]

  private[this] final val _tryTxBoundary: TxBoundary[Try[Any]] = (result, provider, ctx) => {
    result match {
      case Success(_) => provider.commit(ctx)
      case Failure(e) => provider.rollback(ctx.cause(e))
    }
    result
  }


  implicit def futureTxBoundary[T](implicit ec: ExecutionContext): TxBoundary[Future[T]] = (result, provider, ctx) =>
    result.andThen {
      case Success(_) => provider.commit(ctx)
      case Failure(e) => provider.rollback(ctx.cause(e))
    }

}
