package com.github.kxbmap.jooqs

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
    db.withTransaction { implicit session =>
      dsl.createTable(USER)
        .column(ID, ID.getDataType)
        .column(NAME, NAME.getDataType)
        .execute()
    }
  }

  after {
    db.withTransaction { implicit session =>
      dsl.deleteFrom(USER).execute()
    }
  }


  def fetchNames(): List[String] = db.withTransaction { implicit session =>
    dsl.selectFrom(USER).fetch(NAME).toList
  }


  describe("Database") {
    describe("withTransaction") {
      describe("provide transactional session") {
        it("commit after block") {
          db.withTransaction { implicit session =>
            dsl.insertInto(USER, ID, NAME)
              .values(1L, "Alice")
              .execute()
          }
          assert(fetchNames() == List("Alice"))
        }

        it("rollback when exception raised") {
          intercept[DummyException] {
            db.withTransaction[Int] { implicit session =>
              dsl.insertInto(USER, ID, NAME)
                .values(1L, "Alice")
                .execute()

              assert(dsl.selectFrom(USER).fetch().size() == 1)

              throw new DummyException()
            }
          }
          assert(fetchNames() == Nil)
        }

        it("commit when `return`") {
          def method(): Unit = {
            db.withTransaction[Unit] { implicit session =>
              dsl.insertInto(USER, ID, NAME)
                .values(1L, "Alice")
                .execute()
              return
            }
          }
          method()
          assert(fetchNames() == List("Alice"))
        }

        describe("with Try boundary") {
          it("commit if success") {
            db.withTransaction { implicit session =>
              Try {
                dsl.insertInto(USER, ID, NAME)
                  .values(1L, "Alice")
                  .execute()
              }
            }
            assert(fetchNames() == List("Alice"))
          }

          it("rollback if failure") {
            db.withTransaction { implicit session =>
              Try[Int] {
                dsl.insertInto(USER, ID, NAME)
                  .values(1L, "Alice")
                  .execute()

                throw new DummyException()
              }
            }
            assert(fetchNames() == Nil)
          }
        }

        describe("with Future boundary") {

          import scala.concurrent.ExecutionContext.Implicits.global

          it("commit if success") {
            val f = db.withTransaction { implicit session =>
              Future {
                dsl.insertInto(USER, ID, NAME)
                  .values(1L, "Alice")
                  .execute()
              }
            }
            Await.ready(f, Duration.Inf)

            assert(fetchNames() == List("Alice"))
          }

          it("rollback if failure") {
            val f = db.withTransaction { implicit session =>
              Future[Int] {
                dsl.insertInto(USER, ID, NAME)
                  .values(1L, "Alice")
                  .execute()

                throw new DummyException()
              }
            }
            Await.ready(f, Duration.Inf)

            assert(fetchNames() == Nil)
          }
        }
      }

      describe("with savepoint") {
        it("commit savepoint") {
          db.withTransaction { implicit session =>
            dsl.insertInto(USER, ID, NAME)
              .values(1L, "Alice")
              .execute()

            savepoint {
              dsl.insertInto(USER, ID, NAME)
                .values(2L, "Bob")
                .execute()
            }

            dsl.insertInto(USER, ID, NAME)
              .values(3L, "Charlie")
              .execute()
          }
          assert(fetchNames() == List("Alice", "Bob", "Charlie"))
        }

        it("rollback savepoint") {
          db.withTransaction { implicit session =>
            dsl.insertInto(USER, ID, NAME)
              .values(1L, "Alice")
              .execute()

            try
              savepoint[Int] {
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
          assert(fetchNames() == List("Alice", "Charlie"))
        }

        it("rollback transaction") {
          intercept[DummyException] {
            db.withTransaction { implicit session =>
              dsl.insertInto(USER, ID, NAME)
                .values(1L, "Alice")
                .execute()

              savepoint[Int] {
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
          assert(fetchNames() == Nil)
        }

        it("nested") {
          db.withTransaction { implicit session =>
            dsl.insertInto(USER, ID, NAME)
              .values(1L, "Alice")
              .execute()

            savepoint {
              dsl.insertInto(USER, ID, NAME)
                .values(2L, "Bob")
                .execute()

              try
                savepoint[Int] {
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
          assert(fetchNames() == List("Alice", "Bob", "Dave", "Ellen"))
        }

        describe("with Try boundary") {
          it("commit savepoint if success") {
            db.withTransaction { implicit session =>
              dsl.insertInto(USER, ID, NAME)
                .values(1L, "Alice")
                .execute()

              savepoint {
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
            assert(fetchNames() == List("Alice", "Bob", "Charlie"))
          }

          it("rollback savepoint if failure") {
            db.withTransaction { implicit session =>
              dsl.insertInto(USER, ID, NAME)
                .values(1L, "Alice")
                .execute()

              savepoint {
                Try[Int] {
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
            assert(fetchNames() == List("Alice", "Charlie"))
          }
        }

        describe("with Future boundary") {

          import scala.concurrent.ExecutionContext.Implicits.global

          it("commit savepoint if success") {
            val f = db.withTransaction { implicit session =>
              dsl.insertInto(USER, ID, NAME)
                .values(1L, "Alice")
                .execute()

              savepoint {
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

            assert(fetchNames() == List("Alice", "Bob", "Charlie"))
          }

          it("rollback savepoint if failure") {
            val f = db.withTransaction { implicit session =>
              dsl.insertInto(USER, ID, NAME)
                .values(1L, "Alice")
                .execute()

              savepoint {
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

            assert(fetchNames() == List("Alice", "Charlie"))
          }
        }
      }
    }

    describe("withSession") {
      it("commit each statement") {
        intercept[DummyException] {
          db.withSession { implicit session =>
            dsl.insertInto(USER, ID, NAME)
              .values(1L, "Alice")
              .execute()

            dsl.insertInto(USER, ID, NAME)
              .values(2L, "Bob")
              .execute()

            throw new DummyException()
          }
        }
        assert(fetchNames() == List("Alice", "Bob"))
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

            assert(fetchNames() == List("Alice"))

            dsl.insertInto(USER, ID, NAME)
              .values(2L, "Bob")
              .execute()

            assert(fetchNames() == List("Alice", "Bob"))
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

            s.commit()
          } finally {
            s.close()
          }
          assert(fetchNames() == List("Alice", "Bob"))
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
          assert(fetchNames() == Nil)
        }
      }
    }
  }

}
