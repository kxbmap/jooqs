package com.github.kxbmap.jooqs.db

import com.github.kxbmap.jooqs.syntax._
import org.jooq.{TransactionContext, TransactionProvider}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal
import scala.util.{Failure, Success, Try}

trait TxBoundary[T] {

  def onFinish(result: T, provider: TransactionProvider, ctx: TransactionContext): T

  def onError(error: Throwable, provider: TransactionProvider, ctx: TransactionContext): T
}

object TxBoundary extends TxBoundaryInstances {

  def apply[T](implicit b: TxBoundary[T]): TxBoundary[T] = b

  // data keys
  private[jooqs] final val RollbackCalled = "com.github.kxbmap.jooqs.db.TxBoundary.RollbackCalled"

}


sealed trait TxBoundaryInstances extends LowPriorityTxBoundaryInstances {

  implicit def exceptionTxBoundary[T]: TxBoundary[T] = _exceptionTxBoundary.asInstanceOf[TxBoundary[T]]

  private[this] final val _exceptionTxBoundary: TxBoundary[Any] = new TxBoundary[Any] {
    def onFinish(result: Any, provider: TransactionProvider, ctx: TransactionContext): Any = {
      try {
        provider.commit(ctx)
        result
      } catch {
        case NonFatal(ce) =>
          try
            provider.rollback(ctx.cause(ce))
          catch {
            case NonFatal(re) =>
              ce.addSuppressed(re)
          }
          finally
            ctx.data(TxBoundary.RollbackCalled, true)

          throw ce
      }
    }

    def onError(error: Throwable, provider: TransactionProvider, ctx: TransactionContext): Any = {
      if (ctx.data(TxBoundary.RollbackCalled) == null) {
        try
          provider.rollback(ctx.cause(error))
        catch {
          case NonFatal(re) =>
            re.addSuppressed(error)
            throw re
        }
      }
      throw error
    }
  }


  implicit def tryTxBoundary[T]: TxBoundary[Try[T]] = _tryTxBoundary.asInstanceOf[TxBoundary[Try[T]]]

  private[this] final val _tryTxBoundary: TxBoundary[Try[Any]] = new TxBoundary[Try[Any]] {

    def onFinish(result: Try[Any], provider: TransactionProvider, ctx: TransactionContext): Try[Any] = {
      result match {
        case Success(_) =>
          try
            provider.commit(ctx)
          catch {
            case NonFatal(ce) =>
              try
                provider.rollback(ctx.cause(ce))
              catch {
                case NonFatal(re) =>
                  ce.addSuppressed(re)
              }
              finally
                ctx.data(TxBoundary.RollbackCalled, true)

              throw ce
          }

        case Failure(e) =>
          try
            provider.rollback(ctx.cause(e))
          catch {
            case NonFatal(re) =>
              re.addSuppressed(e)
              throw re
          }
          finally
            ctx.data(TxBoundary.RollbackCalled, true)
      }
      result
    }

    def onError(error: Throwable, provider: TransactionProvider, ctx: TransactionContext): Try[Any] = {
      if (ctx.data(TxBoundary.RollbackCalled) == null) {
        try
          provider.rollback(ctx.cause(error))
        catch {
          case NonFatal(re) =>
            re.addSuppressed(error)
            throw re
        }
      }
      throw error
    }
  }


  implicit def futureTxBoundary[T](implicit ec: ExecutionContext): TxBoundary[Future[T]] = new TxBoundary[Future[T]] {
    def onFinish(result: Future[T], provider: TransactionProvider, ctx: TransactionContext): Future[T] =
      result.andThen {
        case Success(_) => provider.commit(ctx)
        case Failure(e) => provider.rollback(ctx.cause(e))
      }

    def onError(error: Throwable, provider: TransactionProvider, ctx: TransactionContext): Future[T] = {
      try
        provider.rollback(ctx.cause(error))
      catch {
        case NonFatal(re) =>
          re.addSuppressed(error)
          throw re
      }
      throw error
    }
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
