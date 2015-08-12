package com.github.kxbmap.jooqs.play

import com.github.kxbmap.configs.syntax._
import com.github.kxbmap.jooqs.Database
import com.github.kxbmap.jooqs.config._
import com.typesafe.config.Config
import org.jooq.conf.SettingsTools
import org.jooq.tools.jdbc.JDBCUtils
import play.api.db.DBApi

class DefaultJooqDBApi(config: Config, dbApi: DBApi) extends JooqDBApi {

  private lazy val databaseByName: Map[String, Database] = {
    val dbKey = config.getString("play.db.config")
    dbApi.databases().map { p =>
      val path = s"$dbKey.${p.name}.jooq.settings"
      val settings = config.getOrElse(path, SettingsTools.defaultSettings())
      p.name -> Database(p.dataSource, JDBCUtils.dialect(p.url), settings)
    }(collection.breakOut)
  }

  lazy val databases: Seq[Database] =
    databaseByName.valuesIterator.toList

  def database(name: String): Database =
    databaseByName.getOrElse(name, throw new IllegalArgumentException(s"Could not find database for $name"))

  def shutdown(): Unit = {
    databases.foreach(_.shutdown())
  }
}
