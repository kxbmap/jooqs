package jooqs.play

import com.github.kxbmap.configs.syntax._
import com.typesafe.config.Config
import jooqs.Database
import jooqs.config._
import org.jooq.conf.SettingsTools
import org.jooq.tools.jdbc.JDBCUtils
import play.api.db.DBApi

class DefaultJooqDBApi(config: Config, dbApi: DBApi) extends JooqDBApi {

  private lazy val databaseByName: Map[String, Database] = {
    val dbKey = config.getString("play.db.config")
    dbApi.databases().map { p =>
      val path = s"$dbKey.${p.name}.jooq"
      val db = Database(
        p.dataSource,
        dialect = config.getOrElse(s"$path.dialect", JDBCUtils.dialect(p.url)),
        settings = config.getOrElse(path, SettingsTools.defaultSettings())
      )
      p.name -> db
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
