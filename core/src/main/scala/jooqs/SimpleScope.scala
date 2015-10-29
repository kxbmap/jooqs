package jooqs

import org.jooq.conf.Settings
import org.jooq.{Configuration, SQLDialect, Scope}

trait SimpleScope extends Scope {

  def configuration: Configuration

  def settings: Settings = configuration.settings()

  def dialect: SQLDialect = configuration.dialect()

  def family: SQLDialect = dialect.family()

  lazy val data: java.util.Map[AnyRef, AnyRef] = new java.util.HashMap()

  def data(key: AnyRef): AnyRef = data.get(key)

  def data(key: AnyRef, value: AnyRef): AnyRef = data.put(key, value)
}
