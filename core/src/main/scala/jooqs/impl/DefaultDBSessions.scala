package jooqs.impl

import java.sql.Connection
import jooqs.syntax._
import jooqs.{TxBoundary, TxDBSession, UnmanagedDBSession}
import org.jooq.impl.DSL
import org.jooq.{Configuration, DSLContext}
import scala.util.DynamicVariable

private[jooqs] class DefaultTxDBSession(top: Configuration) extends TxDBSession {

  private[this] val dynVar = new DynamicVariable((top, DSL.using(top)))

  def configuration: Configuration = dynVar.value._1

  protected[jooqs] def dsl: DSLContext = dynVar.value._2

  protected[jooqs] def savepoint[T: TxBoundary](block: => T): T =
    dsl.withTransaction { config =>
      dynVar.withValue((config, DSL.using(config))) {
        block
      }
    }
}


private[jooqs] class DefaultUnmanagedDBSession(connection: Connection, c: Configuration) extends UnmanagedDBSession {

  lazy val configuration: Configuration = c.derive(connection)

  protected[jooqs] lazy val dsl: DSLContext = DSL.using(configuration)

  def commit(): Unit = connection.commit()

  def rollback(): Unit = connection.rollback()

  def close(): Unit = connection.close()
}
