package com.github.kxbmap.jooqs.db

import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import org.jooq.SQLDialect
import org.scalatest.{Outcome, fixture}

trait DatabaseFixture {
  this: fixture.Suite =>

  type FixtureParam = Database

  protected def withFixture(test: OneArgTest): Outcome = {
    val config = new HikariConfig()
    val name = test.name.replaceAll("[^a-zA-Z]", "_")
    config.setDataSourceClassName("org.h2.jdbcx.JdbcDataSource")
    config.addDataSourceProperty("URL", s"jdbc:h2:mem:$name")
    val dataSource = new HikariDataSource(config)
    try {
      val db = Database(dataSource, SQLDialect.H2)
      populateDatabase(db)
      withFixture(test.toNoArgTest(db))
    } finally {
      dataSource.close()
    }
  }

  def populateDatabase(db: Database): Unit = {}

}
