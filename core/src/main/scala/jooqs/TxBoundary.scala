package jooqs

import org.jooq.{TransactionContext, TransactionProvider}
import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.control.NonFatal
import scala.util.{Failure, Success, Try}

trait TxBoundary[T] {

  def finish(result: T, provider: TransactionProvider, ctx: TransactionContext): T

}

object TxBoundary extends TxBoundaryInstances {

  def apply[T](implicit b: TxBoundary[T]): TxBoundary[T] = b


  private[jooqs] def commit(provider: TransactionProvider, ctx: TransactionContext): Unit =
    try
      provider.commit(ctx)
    catch {
      case NonFatal(ce) =>
        try
          provider.rollback(ctx.cause(ce))
        catch {
          case NonFatal(re) => ce.addSuppressed(re)
        }
        throw ce
    }

  private[jooqs] def rollback(cause: Throwable, provider: TransactionProvider, ctx: TransactionContext): Unit =
    try
      provider.rollback(ctx.cause(cause))
    catch {
      case NonFatal(re) =>
        re.addSuppressed(cause)
        throw re
    }

}


sealed trait TxBoundaryInstances extends LowPriorityTxBoundaryInstances {

  implicit def exceptionTxBoundary[T]: TxBoundary[T] = _exceptionTxBoundary.asInstanceOf[TxBoundary[T]]

  private[this] final val _exceptionTxBoundary: TxBoundary[Any] = (result, provider, ctx) => {
    TxBoundary.commit(provider, ctx)
    result
  }


  implicit def tryTxBoundary[T]: TxBoundary[Try[T]] = _tryTxBoundary.asInstanceOf[TxBoundary[Try[T]]]

  private[this] final val _tryTxBoundary: TxBoundary[Try[Any]] = (result, provider, ctx) =>
    try {
      result match {
        case Success(_) => TxBoundary.commit(provider, ctx)
        case Failure(e) => TxBoundary.rollback(e, provider, ctx)
      }
      result
    } catch {
      case NonFatal(e) => Failure(e)
    }


  implicit def futureTxBoundary[T](implicit ec: ExecutionContext): TxBoundary[Future[T]] = (result, provider, ctx) => {
    val p = Promise[T]()
    result.onComplete { t =>
      p.complete(tryTxBoundary[T].finish(t, provider, ctx))
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
