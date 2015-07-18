package com.github.kxbmap.jooqs.db

import org.jooq.exception.DataAccessException
import org.jooq.impl.DefaultDSLContext
import org.jooq.{Configuration, TransactionContext, TransactionProvider, TransactionalCallable}
import scala.util.control.{ControlThrowable, NonFatal}

@SerialVersionUID(1L)
class ScalaDSLContext(configuration: Configuration) extends DefaultDSLContext(configuration) {

  override def transactionResult[T](transactional: TransactionalCallable[T]): T = {
    val ctx = new DefaultTransactionContext(configuration.derive())
    val provider = ctx.configuration.transactionProvider()
    try {
      provider.begin(ctx)
      val result = transactional.run(ctx.configuration)
      ctx.configuration.data(TxBoundary.Key) match {
        case null                       => provider.commit(ctx)
        case b: TxBoundary[T@unchecked] => b.finish(result, provider, ctx)
      }
      result
    } catch {
      case control: ControlThrowable =>
        provider.commit(ctx)
        throw control

      case cause: Exception =>
        rollback(cause, provider, ctx.cause(cause)) match {
          case e: RuntimeException => throw e
          case e                   => throw new DataAccessException("Rollback caused", e)
        }

      case cause: Throwable =>
        throw rollback(cause, provider, ctx)
    }
  }

  private def rollback[E <: Throwable](cause: E, provider: TransactionProvider, ctx: TransactionContext): E = {
    try {
      provider.rollback(ctx)
    } catch {
      case NonFatal(e) => cause.addSuppressed(e)
    }
    cause
  }

}
