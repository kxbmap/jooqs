package jooqs.impl

import java.sql.Connection
import jooqs._
import jooqs.syntax._
import org.jooq.{Configuration, DSLContext}

private[jooqs] class DefaultDatabase(dsl: DSLContext) extends Database {

  val configuration: Configuration = dsl.configuration()

  def withTransaction[T: TxBoundary](block: TxDBSession => T): T =
    dsl.withTransaction { config =>
      block(new DefaultTxDBSession(config))
    }

  def withSession[T](block: DBSession => T): T = {
    val session = getSession()
    try
      block(session)
    finally
      session.close()
  }

  def getSession(autoCommit: Boolean = true): UnmanagedDBSession =
    new DefaultUnmanagedDBSession(getConnection(autoCommit), configuration)

  private def getConnection(autoCommit: Boolean = true): Connection = {
    val conn = new ProvidedConnection(configuration.connectionProvider())
    if (autoCommit != conn.getAutoCommit) {
      conn.setAutoCommit(autoCommit)
    }
    conn
  }

  def close(): Unit = {
    dsl.close()
  }

}
