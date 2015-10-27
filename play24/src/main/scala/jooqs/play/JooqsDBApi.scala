package jooqs.play

import jooqs.Database

trait JooqsDBApi {

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
