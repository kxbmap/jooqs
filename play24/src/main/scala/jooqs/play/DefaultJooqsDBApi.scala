package jooqs.play

import com.typesafe.config.Config
import configs.Result
import configs.syntax._
import jooqs.Database
import jooqs.config._
import org.jooq.conf.SettingsTools
import org.jooq.tools.jdbc.JDBCUtils
import play.api.db.DBApi

class DefaultJooqsDBApi(config: Config, dbApi: DBApi) extends JooqsDBApi {

  private lazy val databaseByName: Map[String, Database] =
    (for {
      dbKey <- config.get[String]("play.db.config")
      dbs <- Result.traverse(dbApi.databases()) { p =>
        val path = s"$dbKey.${p.name}.jooq"
        val dialect = config.getOrElse(s"$path.dialect", JDBCUtils.dialect(p.url))
        val settings = config.getOrElse(path, SettingsTools.defaultSettings())
        (dialect ~ settings) {
          p.name -> Database(p.dataSource, _, _)
        }
      }
    } yield dbs.toMap).value

  lazy val databases: Seq[Database] =
    databaseByName.valuesIterator.toList

  def database(name: String): Database =
    databaseByName.getOrElse(name, throw new IllegalArgumentException(s"Could not find database for $name"))

  def shutdown(): Unit = {
    databases.foreach(_.close())
  }
}
