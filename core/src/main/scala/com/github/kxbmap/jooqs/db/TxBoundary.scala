package com.github.kxbmap.jooqs.db

import com.github.kxbmap.jooqs.syntax._
import org.jooq.{TransactionContext, TransactionProvider}
import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.control.NonFatal
import scala.util.{Failure, Success, Try}

trait TxBoundary[T] {

  def finish(result: T, provider: TransactionProvider, ctx: TransactionContext): T

}

object TxBoundary extends TxBoundaryInstances {

  def apply[T](implicit b: TxBoundary[T]): TxBoundary[T] = b

}


sealed trait TxBoundaryInstances extends LowPriorityTxBoundaryInstances {

  implicit def exceptionTxBoundary[T]: TxBoundary[T] = _exceptionTxBoundary.asInstanceOf[TxBoundary[T]]

  private[this] final val _exceptionTxBoundary: TxBoundary[Any] = (result, provider, ctx) =>
    try {
      provider.commit(ctx)
      result
    } catch {
      case NonFatal(ce) =>
        try
          provider.rollback(ctx.cause(ce))
        catch {
          case NonFatal(re) => ce.addSuppressed(re)
        }
        throw ce
    }


  implicit def tryTxBoundary[T]: TxBoundary[Try[T]] = _tryTxBoundary.asInstanceOf[TxBoundary[Try[T]]]

  private[this] final val _tryTxBoundary: TxBoundary[Try[Any]] = (result, provider, ctx) =>
    result match {
      case Success(_) =>
        try {
          provider.commit(ctx)
          result
        } catch {
          case NonFatal(ce) =>
            try
              provider.rollback(ctx.cause(ce))
            catch {
              case NonFatal(re) => ce.addSuppressed(re)
            }
            Failure(ce)
        }

      case Failure(e) =>
        try {
          provider.rollback(ctx.cause(e))
          result
        } catch {
          case NonFatal(re) =>
            re.addSuppressed(e)
            Failure(re)
        }
    }


  implicit def futureTxBoundary[T](implicit ec: ExecutionContext): TxBoundary[Future[T]] = (result, provider, ctx) => {
    val p = Promise[T]()
    result.onComplete {
      case s@Success(_) =>
        val r = try {
          provider.commit(ctx)
          s
        } catch {
          case NonFatal(ce) =>
            try
              provider.rollback(ctx.cause(ce))
            catch {
              case NonFatal(re) => ce.addSuppressed(re)
            }
            Failure(ce)
        }
        p.complete(r)

      case f@Failure(e) =>
        val r = try {
          provider.rollback(ctx.cause(e))
          f
        } catch {
          case NonFatal(re) =>
            re.addSuppressed(e)
            Failure(re)
        }
        p.complete(r)
    }
    p.future
  }


  implicit def `'cannot apply to Nothing value (amb1)'`: TxBoundary[Nothing] = sys.error("ambiguous TxBoundary used")

  implicit def `'cannot apply to Nothing value (amb2)'`: TxBoundary[Nothing] = sys.error("ambiguous TxBoundary used")

  implicit def `'cannot apply to Try[Nothing] value (amb1)'`: TxBoundary[Try[Nothing]] = sys.error("ambiguous TxBoundary used")

  implicit def `'cannot apply to Try[Nothing] value (amb2)'`: TxBoundary[Try[Nothing]] = sys.error("ambiguous TxBoundary used")

  implicit def `'cannot apply to Future[Nothing] value (amb1)'`: TxBoundary[Future[Nothing]] = sys.error("ambiguous TxBoundary used")

  implicit def `'cannot apply to Future[Nothing] value (amb2)'`: TxBoundary[Future[Nothing]] = sys.error("ambiguous TxBoundary used")

}

sealed trait LowPriorityTxBoundaryInstances {

  implicit def `'could not find implicit ExecutionContext (amb1)'`[T]: TxBoundary[Future[T]] = sys.error("ambiguous TxBoundary used")

  implicit def `'could not find implicit ExecutionContext (amb2)'`[T]: TxBoundary[Future[T]] = sys.error("ambiguous TxBoundary used")

}
