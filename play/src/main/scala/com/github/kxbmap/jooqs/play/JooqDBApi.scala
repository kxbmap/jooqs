package com.github.kxbmap.jooqs.play

import com.github.kxbmap.jooqs.Database

trait JooqDBApi {

  /**
   * All configured DSL databases
   */
  def databases(): Seq[Database]

  /**
   * Get database with given configuration name.
   *
   * @param name the configuration name of the database
   */
  def database(name: String): Database

  /**
   * Shutdown all databases, releasing resources.
   */
  def shutdown(): Unit

}
