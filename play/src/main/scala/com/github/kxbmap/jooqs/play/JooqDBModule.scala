package com.github.kxbmap.jooqs.play

import com.github.kxbmap.jooqs.Database
import javax.inject.{Inject, Provider, Singleton}
import play.api.db.DBApi
import play.api.inject.{ApplicationLifecycle, Binding, BindingKey, Module}
import play.api.{Configuration, Environment}
import scala.concurrent.Future

/**
 * jOOQ database runtime inject module
 */
final class JooqDBModule extends Module {
  def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = {
    val dbKey = configuration.underlying.getString("play.db.config")
    val default = configuration.underlying.getString("play.db.default")
    val dbs = configuration.getConfig(dbKey).getOrElse(Configuration.empty).subKeys
    Seq(
      bind[JooqDBApi].toProvider[JooqDBApiProvider]
    ) ++ namedDatabaseBindings(dbs) ++ defaultDatabaseBinding(default, dbs)
  }

  private def bindNamed(name: String): BindingKey[Database] = {
    bind[Database].qualifiedWith(new NamedDatabaseImpl(name))
  }

  private def namedDatabaseBindings(dbs: Set[String]): Seq[Binding[_]] = dbs.toSeq.map { db =>
    bindNamed(db).to(new NamedDatabaseProvider(db))
  }

  private def defaultDatabaseBinding(default: String, dbs: Set[String]): Seq[Binding[_]] = {
    if (dbs.contains(default)) Seq(bind[Database].to(bindNamed(default))) else Nil
  }
}

/**
 * jOOQ database components (for compile-time injection).
 */
trait JooqDBComponent {
  def dbApi: DBApi

  def applicationLifecycle: ApplicationLifecycle

  lazy val jooqDBApi: JooqDBApi = new JooqDBApiProvider(dbApi, applicationLifecycle).get
}


@Singleton
class JooqDBApiProvider @Inject()(dbApi: DBApi, lifecycle: ApplicationLifecycle) extends Provider[JooqDBApi] {
  lazy val get: JooqDBApi = {
    val jooq = new DefaultJooqDBApi(dbApi)
    lifecycle.addStopHook(() => Future.successful(jooq.shutdown()))
    jooq
  }
}

class NamedDatabaseProvider(name: String) extends Provider[Database] {
  @Inject private var jooqDBApi: JooqDBApi = _
  lazy val get: Database = jooqDBApi.database(name)
}
