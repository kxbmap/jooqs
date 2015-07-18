package com.github.kxbmap.jooqs.db

import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import org.jooq.SQLDialect
import org.scalatest.{BeforeAndAfterAll, Suite}

trait InMemoryTestDB extends BeforeAndAfterAll {
  this: Suite =>

  lazy val db = Database(dataSource, SQLDialect.H2)

  private lazy val dataSource = {
    val config = new HikariConfig()
    config.setDataSourceClassName("org.h2.jdbcx.JdbcDataSource")
    config.addDataSourceProperty("URL", s"jdbc:h2:mem:${getClass.getName}")
    new HikariDataSource(config)
  }

  override protected def afterAll(): Unit = {
    dataSource.close()
  }

}
