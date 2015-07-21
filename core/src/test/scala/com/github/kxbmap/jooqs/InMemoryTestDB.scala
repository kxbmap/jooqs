package com.github.kxbmap.jooqs

import java.sql.DriverManager
import org.scalatest.{BeforeAndAfterAll, Suite}

trait InMemoryTestDB extends BeforeAndAfterAll {
  this: Suite =>

  lazy val db = Database(connection)

  private lazy val connection = {
    Class.forName("org.h2.Driver")
    DriverManager.getConnection(s"jdbc:h2:mem:${getClass.getName}")
  }

  override protected def afterAll(): Unit = {
    connection.close()
  }

}
