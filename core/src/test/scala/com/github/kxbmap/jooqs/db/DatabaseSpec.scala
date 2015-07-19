package com.github.kxbmap.jooqs.db

import com.github.kxbmap.jooqs.syntax._
import org.jooq.impl.{DSL, SQLDataType}
import org.scalatest.{BeforeAndAfter, FunSpec}
import scala.collection.JavaConversions._
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.Try
import scala.util.control.NoStackTrace

class DatabaseSpec extends FunSpec with InMemoryTestDB with BeforeAndAfter {

  class DummyException extends Exception with NoStackTrace


  val USER = DSL.table("USER")
  val ID = DSL.field("ID", SQLDataType.BIGINT.nullable(false))
  val NAME = DSL.field("NAME", SQLDataType.VARCHAR.length(255).nullable(false))

  override protected def beforeAll(): Unit = {
    db.withTransaction { implicit s =>
      dsl.createTable(USER)
        .column(ID, ID.getDataType)
        .column(NAME, NAME.getDataType)
        .execute()
    }
  }

  after {
    db.withTransaction { implicit s =>
      dsl.deleteFrom(USER).execute()
    }
  }


  def fetchNames(): List[String] = db.withTransaction { implicit s =>
    dsl.selectFrom(USER).fetch(NAME).toList
  }

  def assertFetchNames(expected: List[String]): Unit = {
    assert(fetchNames() == expected)
  }


  describe("Database") {
    describe("withTransaction") {
      describe("provide transactional session") {
        it("commit after block") {
          db.withTransaction { implicit s =>
            dsl.insertInto(USER, ID, NAME)
              .values(1L, "Alice")
              .execute()
          }
          assertFetchNames(List("Alice"))
        }

        it("rollback when exception raised") {
          intercept[DummyException] {
            db.withTransaction[Int] { implicit s =>
              dsl.insertInto(USER, ID, NAME)
                .values(1L, "Alice")
                .execute()

              assert(dsl.selectFrom(USER).fetch().size() == 1)

              throw new DummyException()
            }
          }
          assertFetchNames(Nil)
        }

        it("commit when `return`") {
          def method(): Unit = {
            db.withTransaction[Unit] { implicit s =>
              dsl.insertInto(USER, ID, NAME)
                .values(1L, "Alice")
                .execute()
              return
            }
          }
          method()
          assertFetchNames(List("Alice"))
        }

        describe("with Try boundary") {
          it("commit if success") {
            db.withTransaction { implicit s =>
              Try {
                dsl.insertInto(USER, ID, NAME)
                  .values(1L, "Alice")
                  .execute()
              }
            }
            assertFetchNames(List("Alice"))
          }

          it("rollback if failure") {
            db.withTransaction { implicit s =>
              Try {
                dsl.insertInto(USER, ID, NAME)
                  .values(1L, "Alice")
                  .execute()

                throw new DummyException()
              }
            }
            assertFetchNames(Nil)
          }
        }

        describe("with Future boundary") {

          import scala.concurrent.ExecutionContext.Implicits.global

          it("commit if success") {
            val f = db.withTransaction { implicit s =>
              Future {
                dsl.insertInto(USER, ID, NAME)
                  .values(1L, "Alice")
                  .execute()
              }
            }
            Await.ready(f, Duration.Inf)

            assertFetchNames(List("Alice"))
          }

          it("rollback if failure") {
            val f = db.withTransaction { implicit s =>
              Future {
                dsl.insertInto(USER, ID, NAME)
                  .values(1L, "Alice")
                  .execute()

                throw new DummyException()
              }
            }
            Await.ready(f, Duration.Inf)

            assertFetchNames(Nil)
          }
        }
      }

      describe("with savepoint") {
        it("commit savepoint") {
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
          assertFetchNames(List("Alice", "Bob", "Charlie"))
        }

        it("rollback savepoint") {
          db.withTransaction { implicit s =>
            dsl.insertInto(USER, ID, NAME)
              .values(1L, "Alice")
              .execute()

            try
              s.savepoint[Int] {
                dsl.insertInto(USER, ID, NAME)
                  .values(2L, "Bob")
                  .execute()

                throw new DummyException()
              }
            catch {
              case _: DummyException =>
            }

            dsl.insertInto(USER, ID, NAME)
              .values(3L, "Charlie")
              .execute()
          }
          assertFetchNames(List("Alice", "Charlie"))
        }

        it("rollback transaction") {
          intercept[DummyException] {
            db.withTransaction { implicit s =>
              dsl.insertInto(USER, ID, NAME)
                .values(1L, "Alice")
                .execute()

              s.savepoint[Int] {
                dsl.insertInto(USER, ID, NAME)
                  .values(2L, "Bob")
                  .execute()

                throw new DummyException()
              }

              dsl.insertInto(USER, ID, NAME)
                .values(3L, "Charlie")
                .execute()
            }
          }
          assertFetchNames(Nil)
        }

        it("nested") {
          db.withTransaction { implicit s =>
            dsl.insertInto(USER, ID, NAME)
              .values(1L, "Alice")
              .execute()

            s.savepoint {
              dsl.insertInto(USER, ID, NAME)
                .values(2L, "Bob")
                .execute()

              try
                s.savepoint[Int] {
                  dsl.insertInto(USER, ID, NAME)
                    .values(3L, "Charlie")
                    .execute()
                  throw new DummyException()
                }
              catch {
                case _: DummyException =>
              }

              dsl.insertInto(USER, ID, NAME)
                .values(4L, "Dave")
                .execute()
            }

            dsl.insertInto(USER, ID, NAME)
              .values(5L, "Ellen")
              .execute()
          }
          assertFetchNames(List("Alice", "Bob", "Dave", "Ellen"))
        }

        describe("with Try boundary") {
          it("commit savepoint if success") {
            db.withTransaction { implicit s =>
              dsl.insertInto(USER, ID, NAME)
                .values(1L, "Alice")
                .execute()

              s.savepoint {
                Try {
                  dsl.insertInto(USER, ID, NAME)
                    .values(2L, "Bob")
                    .execute()
                }
              }

              dsl.insertInto(USER, ID, NAME)
                .values(3L, "Charlie")
                .execute()
            }
            assertFetchNames(List("Alice", "Bob", "Charlie"))
          }

          it("rollback savepoint if failure") {
            db.withTransaction { implicit s =>
              dsl.insertInto(USER, ID, NAME)
                .values(1L, "Alice")
                .execute()

              s.savepoint {
                Try {
                  dsl.insertInto(USER, ID, NAME)
                    .values(2L, "Bob")
                    .execute()

                  throw new DummyException()
                }
              }

              dsl.insertInto(USER, ID, NAME)
                .values(3L, "Charlie")
                .execute()
            }
            assertFetchNames(List("Alice", "Charlie"))
          }
        }

        describe("with Future boundary") {

          import scala.concurrent.ExecutionContext.Implicits.global

          it("commit savepoint if success") {
            val f = db.withTransaction { implicit s =>
              dsl.insertInto(USER, ID, NAME)
                .values(1L, "Alice")
                .execute()

              s.savepoint {
                Future {
                  dsl.insertInto(USER, ID, NAME)
                    .values(2L, "Bob")
                    .execute()
                }
              }.recover {
                case _: DummyException => 42
              }.andThen {
                case _ =>
                  dsl.insertInto(USER, ID, NAME)
                    .values(3L, "Charlie")
                    .execute()
              }
            }
            Await.ready(f, Duration.Inf)

            assertFetchNames(List("Alice", "Bob", "Charlie"))
          }

          it("rollback savepoint if failure") {
            val f = db.withTransaction { implicit s =>
              dsl.insertInto(USER, ID, NAME)
                .values(1L, "Alice")
                .execute()

              s.savepoint {
                Future[Int] {
                  dsl.insertInto(USER, ID, NAME)
                    .values(2L, "Bob")
                    .execute()

                  throw new DummyException()
                }
              }.recover {
                case _: DummyException => 42
              }.andThen {
                case _ =>
                  dsl.insertInto(USER, ID, NAME)
                    .values(3L, "Charlie")
                    .execute()
              }
            }
            Await.ready(f, Duration.Inf)

            assertFetchNames(List("Alice", "Charlie"))
          }
        }
      }
    }

    describe("withSession") {
      it("commit each statement") {
        intercept[DummyException] {
          db.withSession { implicit s =>
            dsl.insertInto(USER, ID, NAME)
              .values(1L, "Alice")
              .execute()

            dsl.insertInto(USER, ID, NAME)
              .values(2L, "Bob")
              .execute()

            throw new DummyException()
          }
        }
        assertFetchNames(List("Alice", "Bob"))
      }
    }

    describe("getSession") {
      describe("with autoCommit") {
        it("commit each statement") {
          implicit val s = db.getSession(autoCommit = true)
          try {
            dsl.insertInto(USER, ID, NAME)
              .values(1L, "Alice")
              .execute()

            assertFetchNames(List("Alice"))

            dsl.insertInto(USER, ID, NAME)
              .values(2L, "Bob")
              .execute()

            assertFetchNames(List("Alice", "Bob"))
          } finally {
            s.close()
          }
        }
      }

      describe("without autoCommit") {
        it("commit") {
          implicit val s = db.getSession(autoCommit = false)
          try {
            dsl.insertInto(USER, ID, NAME)
              .values(1L, "Alice")
              .execute()

            dsl.insertInto(USER, ID, NAME)
              .values(2L, "Bob")
              .execute()

            assertFetchNames(Nil)

            s.commit()
          } finally {
            s.close()
          }
          assertFetchNames(List("Alice", "Bob"))
        }

        it("rollback") {
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
          assertFetchNames(Nil)
        }
      }
    }
  }

}
