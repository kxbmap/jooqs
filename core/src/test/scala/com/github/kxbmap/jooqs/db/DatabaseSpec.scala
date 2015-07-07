package com.github.kxbmap.jooqs.db

import org.jooq.exception.DataAccessException
import org.jooq.impl.{DSL, SQLDataType}
import org.scalatest.fixture
import scala.collection.JavaConversions._

class DatabaseSpec extends fixture.FunSpec with DatabaseFixture {

  import com.github.kxbmap.jooqs.syntax._

  val USER = DSL.table("USER")
  val ID = DSL.field("ID", SQLDataType.BIGINT.nullable(false))
  val NAME = DSL.field("NAME", SQLDataType.VARCHAR.length(255).nullable(false))

  override def populateDatabase(db: Database): Unit = {
    db.withTransaction { implicit s =>
      dsl.createTable(USER)
        .column(ID, ID.getDataType)
        .column(NAME, NAME.getDataType)
        .execute()
    }
  }

  def fetchNames(db: Database): List[String] = db.withTransaction { implicit s =>
    dsl.selectFrom(USER).fetch(NAME).toList
  }

  describe("Database") {
    describe("withTransaction") {
      describe("provide transactional session") {
        it("commit after block") { db =>
          db.withTransaction { implicit s =>
            dsl.insertInto(USER, ID, NAME)
              .values(1L, "Alice")
              .execute()
          }
          assert(fetchNames(db) == List("Alice"))
        }

        it("rollback when exception raised") { db =>
          try
            db.withTransaction { implicit s =>
              dsl.insertInto(USER, ID, NAME)
                .values(1L, "Alice")
                .execute()

              assert(dsl.selectFrom(USER).fetch().size() == 1)

              throw new Exception("will rollback")
            }
          catch {
            case _: DataAccessException =>
          }
          assert(fetchNames(db) == Nil)
        }

        it("commit when `return`") { db =>
          def method(): Unit = {
            db.withTransaction { implicit s =>
              dsl.insertInto(USER, ID, NAME)
                .values(1L, "Alice")
                .execute()
              return
            }
          }
          method()
          assert(fetchNames(db) == List("Alice"))
        }
      }

      describe("with savepoint") {
        it("commit savepoint") { db =>
          db.withTransaction { implicit s =>
            dsl.insertInto(USER, ID, NAME)
              .values(1L, "Alice")
              .execute()

            s.savepoint {
              dsl.insertInto(USER, ID, NAME)
                .values(2L, "Bob")
                .execute()
            }

            dsl.insertInto(USER, ID, NAME)
              .values(3L, "Charlie")
              .execute()
          }
          assert(fetchNames(db) == List("Alice", "Bob", "Charlie"))
        }

        it("rollback savepoint") { db =>
          db.withTransaction { implicit s =>
            dsl.insertInto(USER, ID, NAME)
              .values(1L, "Alice")
              .execute()

            try
              s.savepoint {
                dsl.insertInto(USER, ID, NAME)
                  .values(2L, "Bob")
                  .execute()

                throw new Exception()
              }
            catch {
              case _: Throwable =>
            }

            dsl.insertInto(USER, ID, NAME)
              .values(3L, "Charlie")
              .execute()
          }
          assert(fetchNames(db) == List("Alice", "Charlie"))
        }

        it("rollback transaction") { db =>
          intercept[DataAccessException] {
            db.withTransaction { implicit s =>
              dsl.insertInto(USER, ID, NAME)
                .values(1L, "Alice")
                .execute()

              s.savepoint {
                dsl.insertInto(USER, ID, NAME)
                  .values(2L, "Bob")
                  .execute()

                throw new Exception()
              }

              dsl.insertInto(USER, ID, NAME)
                .values(3L, "Charlie")
                .execute()
            }
          }
          assert(fetchNames(db) == Nil)
        }

        it("nested") { db =>
          db.withTransaction { implicit s =>
            dsl.insertInto(USER, ID, NAME)
              .values(1L, "Alice")
              .execute()

            s.savepoint {
              dsl.insertInto(USER, ID, NAME)
                .values(2L, "Bob")
                .execute()

              try
                s.savepoint {
                  dsl.insertInto(USER, ID, NAME)
                    .values(3L, "Charlie")
                    .execute()
                  throw new Exception
                }
              catch {
                case _: Throwable =>
              }

              dsl.insertInto(USER, ID, NAME)
                .values(4L, "Dave")
                .execute()
            }

            dsl.insertInto(USER, ID, NAME)
              .values(5L, "Ellen")
              .execute()
          }
          assert(fetchNames(db) == List("Alice", "Bob", "Dave", "Ellen"))
        }
      }
    }

    describe("withSession") {
      it("commit each statement") { db =>
        try
          db.withSession { implicit s =>
            dsl.insertInto(USER, ID, NAME)
              .values(1L, "Alice")
              .execute()

            dsl.insertInto(USER, ID, NAME)
              .values(2L, "Bob")
              .execute()

            throw new Exception
          }
        catch {
          case _: Throwable =>
        }
        assert(fetchNames(db) == List("Alice", "Bob"))
      }
    }

    describe("getSession") {
      describe("with autoCommit") {
        it("commit each statement") { db =>
          implicit val s = db.getSession(autoCommit = true)
          try {
            dsl.insertInto(USER, ID, NAME)
              .values(1L, "Alice")
              .execute()

            assert(fetchNames(db) == List("Alice"))

            dsl.insertInto(USER, ID, NAME)
              .values(2L, "Bob")
              .execute()

            assert(fetchNames(db) == List("Alice", "Bob"))
          } finally {
            s.close()
          }
        }
      }

      describe("without autoCommit") {
        it("commit") { db =>
          implicit val s = db.getSession(autoCommit = false)
          try {
            dsl.insertInto(USER, ID, NAME)
              .values(1L, "Alice")
              .execute()

            dsl.insertInto(USER, ID, NAME)
              .values(2L, "Bob")
              .execute()

            assert(fetchNames(db) == Nil)

            s.commit()
          } finally {
            s.close()
          }
          assert(fetchNames(db) == List("Alice", "Bob"))
        }

        it("rollback") { db =>
          implicit val s = db.getSession(autoCommit = false)
          try {
            dsl.insertInto(USER, ID, NAME)
              .values(1L, "Alice")
              .execute()

            dsl.insertInto(USER, ID, NAME)
              .values(2L, "Bob")
              .execute()

            s.rollback()
          } finally {
            s.close()
          }
          assert(fetchNames(db) == Nil)
        }
      }
    }
  }

}
