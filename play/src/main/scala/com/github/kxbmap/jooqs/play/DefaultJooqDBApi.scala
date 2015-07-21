package com.github.kxbmap.jooqs.play

import com.github.kxbmap.jooqs.Database
import org.jooq.tools.jdbc.JDBCUtils
import play.api.db.DBApi

class DefaultJooqDBApi(dbApi: DBApi) extends JooqDBApi {

  private lazy val databaseByName: Map[String, Database] =
    dbApi.databases().map { p =>
      // TODO load jOOQ Settings per database
      // val settings = SettingsTools.defaultSettings()
      val db = Database(p.dataSource, JDBCUtils.dialect(p.url))
      db.data(JooqDBApi.Name, p.name)
      db.data(JooqDBApi.URL, p.url)
      p.name -> db
    }(collection.breakOut)

  lazy val databases: Seq[Database] =
    databaseByName.valuesIterator.toList

  def database(name: String): Database =
    databaseByName.getOrElse(name, throw new IllegalArgumentException(s"Could not find database for $name"))

  def shutdown(): Unit = {
    databases.foreach(_.shutdown())
  }
}
