package com.github.kxbmap.jooqs

import com.github.kxbmap.jooqs.impl.DefaultDatabase
import java.sql.Connection
import java.util.Properties
import javax.sql.DataSource
import org.jooq.conf.Settings
import org.jooq.impl.DSL
import org.jooq.{ConnectionProvider, DSLContext, SQLDialect}
import scala.util.control.NonFatal

trait Database extends SimpleScope {

  def withTransaction[T: TxBoundary](block: TxDBSession => T): T

  def withSession[T](block: DBSession => T): T

  def getSession(autoCommit: Boolean = true): UnmanagedDBSession

  def shutdown(): Unit
}

object Database {

  def apply(url: String): Database = closeOnShutdown(DSL.using(url))

  def apply(url: String, user: String, password: String): Database = closeOnShutdown(DSL.using(url, user, password))

  def apply(url: String, properties: Properties): Database = closeOnShutdown(DSL.using(url, properties))

  def apply(connection: Connection): Database = Database(DSL.using(connection))

  def apply(connection: Connection, dialect: SQLDialect): Database = Database(DSL.using(connection, dialect))

  def apply(connection: Connection, settings: Settings): Database = Database(DSL.using(connection, settings))

  def apply(connection: Connection, dialect: SQLDialect, settings: Settings): Database = Database(DSL.using(connection, dialect, settings))

  def apply(dataSource: DataSource, dialect: SQLDialect): Database = Database(DSL.using(dataSource, dialect))

  def apply(dataSource: DataSource, dialect: SQLDialect, settings: Settings): Database = Database(DSL.using(dataSource, dialect, settings))

  def apply(connectionProvider: ConnectionProvider, dialect: SQLDialect): Database = Database(DSL.using(connectionProvider, dialect))

  def apply(connectionProvider: ConnectionProvider, dialect: SQLDialect, settings: Settings): Database = Database(DSL.using(connectionProvider, dialect, settings))

  def apply(ctx: DSLContext): Database = new DefaultDatabase(ctx)

  private def closeOnShutdown(ctx: DSLContext): Database = new DefaultDatabase(ctx) with CloseOnShutdown

  private trait CloseOnShutdown extends Database {
    abstract override def shutdown(): Unit = {
      try {
        configuration.connectionProvider().acquire().close()
      } catch {
        case NonFatal(_) =>
      }
      super.shutdown()
    }
  }

}
