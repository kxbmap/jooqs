package com.github.kxbmap.jooqs

import org.jooq._
import org.jooq.impl.DSL
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.FunSpec
import org.scalatest.mock.MockitoSugar
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.Try
import scala.util.control.NoStackTrace

class DSLContextOpsSpec extends FunSpec with MockitoSugar {

  import syntax._

  describe("DSLContextOps") {

    describe("select") {

      val dsl = DSL.using(SQLDialect.DEFAULT)

      it("should accept Array[Field[_]]") {
        val fields = Array[Field[_]](DSL.field("f1"), DSL.field("f2"), DSL.field("f3"))
        assert(dsl.select(fields) == dsl.select(fields: _*))
      }

      it("should accept Seq[Field[_]]") {
        val fields = Seq(DSL.field("f1"), DSL.field("f2"), DSL.field("f3"))
        assert(dsl.select(fields) == dsl.select(fields: _*))
      }
    }


    describe("withTransaction") {

      import scala.concurrent.ExecutionContext.Implicits.global

      implicit def futureAwait[T]: Future[T] => Unit = Await.ready(_, Duration.Inf)

      describe("flow of commit") {

        def flow[T](tx: DSLContext => T)(implicit await: T => Unit = (_: T) => ()): T = {
          val dsl = mock[DSLContext](RETURNS_DEEP_STUBS)
          val provider = mock[TransactionProvider]

          when(dsl.configuration().derive().transactionProvider()).thenReturn(provider)

          val result = tx(dsl)
          await(result)

          val o = inOrder(provider)
          o.verify(provider).begin(any())
          o.verify(provider).commit(any())
          verifyNoMoreInteractions(provider)

          result
        }

        it("should verified with exception boundary") {
          val r = flow { dsl =>
            dsl.withTransaction { _ => 42 }
          }
          assert(r == 42)
        }

        it("should verified with try boundary") {
          val r = flow { dsl =>
            dsl.withTransaction { _ => Try(42) }
          }
          assert(r.isSuccess)
        }

        it("should verified with future boundary") {
          val r = flow { dsl =>
            dsl.withTransaction { _ => Future.successful(42) }
          }
          assert(r.value.exists(_.isSuccess))
        }

      }

      describe("flow of rollback") {

        def flow[T](tx: DSLContext => T)(implicit await: T => Unit = (_: T) => ()): T = {
          val dsl = mock[DSLContext](RETURNS_DEEP_STUBS)
          val provider = mock[TransactionProvider]

          when(dsl.configuration().derive().transactionProvider()).thenReturn(provider)

          val result = tx(dsl)
          await(result)

          val o = inOrder(provider)
          o.verify(provider).begin(any())
          o.verify(provider).rollback(any())
          verifyNoMoreInteractions(provider)

          result
        }

        it("should verified with exception boundary") {
          flow { dsl =>
            intercept[DummyException] {
              dsl.withTransaction[Int] { _ => throw new DummyException }
            }
          }
        }

        it("should verified with try boundary") {
          val r = flow { dsl =>
            dsl.withTransaction { _ => Try[Int] { throw new DummyException } }
          }
          assert(r.isFailure)
          assert(r.failed.get.getMessage == "dummy")
        }

        it("should verified with future boundary") {
          val r = flow { dsl =>
            dsl.withTransaction { _ => Future.failed[Int](new DummyException) }
          }
          assert(r.value.exists(_.isFailure))
          assert(r.value.get.failed.get.getMessage == "dummy")
        }

        describe("with uncaught exception") {

          it("should verified with try boundary") {
            flow { dsl =>
              intercept[DummyException] {
                dsl.withTransaction { _ =>
                  throw new DummyException
                  Try(42)
                }
              }
            }
          }

          it("should verified with future boundary") {
            flow { dsl =>
              intercept[DummyException] {
                dsl.withTransaction { _ =>
                  throw new DummyException
                  Future.successful(42)
                }
              }
            }
          }
        }

      }

      describe("flow of commit to fail") {

        def flow[T](tx: DSLContext => T)(implicit await: T => Unit = (_: T) => ()): T = {
          val dsl = mock[DSLContext](RETURNS_DEEP_STUBS)
          val provider = mock[TransactionProvider]

          when(dsl.configuration().derive().transactionProvider()).thenReturn(provider)
          when(provider.commit(any())).thenThrow(new DummyCommitFailedException)

          val result = tx(dsl)
          await(result)

          val o = inOrder(provider)
          o.verify(provider).begin(any())
          o.verify(provider).commit(any())
          o.verify(provider).rollback(any())
          verifyNoMoreInteractions(provider)

          result
        }

        it("should verified with exception boundary") {
          flow { dsl =>
            intercept[DummyCommitFailedException] {
              dsl.withTransaction { _ => 42 }
            }
          }
        }

        it("should verified with try boundary") {
          val t = flow { dsl =>
            dsl.withTransaction { _ => Try(42) }
          }
          assert(t.isFailure)
          assert(t.failed.get.getMessage == "commit failed")
        }

        it("should verified with future boundary") {
          val f = flow { dsl =>
            dsl.withTransaction { _ => Future.successful(42) }
          }
          assert(f.value.exists(_.isFailure))
          assert(f.value.get.failed.get.getMessage == "commit failed")
        }

      }

      describe("flow of rollback to fail") {

        def flow[T](tx: DSLContext => T)(implicit await: T => Unit = (_: T) => ()): T = {
          val dsl = mock[DSLContext](RETURNS_DEEP_STUBS)
          val provider = mock[TransactionProvider]

          when(dsl.configuration().derive().transactionProvider()).thenReturn(provider)
          when(provider.rollback(any())).thenThrow(new DummyRollbackFailedException)

          val result = tx(dsl)
          await(result)

          val o = inOrder(provider)
          o.verify(provider).begin(any())
          o.verify(provider).rollback(any())
          verifyNoMoreInteractions(provider)

          result
        }

        it("should verified with exception boundary") {
          val e = flow { dsl =>
            intercept[DummyRollbackFailedException] {
              dsl.withTransaction[Int] { _ =>
                throw new DummyException
              }
            }
          }
          assert(e.getSuppressed.exists(_.getMessage == "dummy"))
        }

        it("should verified with try boundary") {
          val t = flow { dsl =>
            dsl.withTransaction { _ =>
              Try[Int] {
                throw new DummyException
              }
            }
          }
          assert(t.isFailure)
          val e = t.failed.get
          assert(e.getMessage == "rollback failed")
          assert(e.getSuppressed.exists(_.getMessage == "dummy"))
        }

        it("should verified with future boundary") {
          val f = flow { dsl =>
            dsl.withTransaction { _ =>
              Future.failed[Int](new DummyException)
            }
          }
          assert(f.value.exists(_.isFailure))
          val e = f.value.get.failed.get
          assert(e.getMessage == "rollback failed")
          assert(e.getSuppressed.exists(_.getMessage == "dummy"))
        }

        describe("with uncaught exception") {

          it("should verified with try boundary") {
            val e = flow { dsl =>
              intercept[DummyRollbackFailedException] {
                dsl.withTransaction { _ =>
                  throw new DummyException
                  Try(42)
                }
              }
            }
            assert(e.getSuppressed.exists(_.getMessage == "dummy"))
          }

          it("should verified with future boundary") {
            val e = flow { dsl =>
              intercept[DummyRollbackFailedException] {
                dsl.withTransaction { _ =>
                  throw new DummyException
                  Future.successful(42)
                }
              }
            }
            assert(e.getSuppressed.exists(_.getMessage == "dummy"))
          }
        }

      }

      describe("flow of both commit and rollback to fail") {

        def flow[T](tx: DSLContext => T)(implicit await: T => Unit = (_: T) => ()): T = {
          val dsl = mock[DSLContext](RETURNS_DEEP_STUBS)
          val provider = mock[TransactionProvider]

          when(dsl.configuration().derive().transactionProvider()).thenReturn(provider)
          when(provider.commit(any())).thenThrow(new DummyCommitFailedException)
          when(provider.rollback(any())).thenThrow(new DummyRollbackFailedException)

          val result = tx(dsl)
          await(result)

          val o = inOrder(provider)
          o.verify(provider).begin(any())
          o.verify(provider).commit(any())
          o.verify(provider).rollback(any())
          verifyNoMoreInteractions(provider)

          result
        }

        it("should verified with exception boundary") {
          val e = flow { dsl =>
            intercept[DummyCommitFailedException] {
              dsl.withTransaction { _ => 42 }
            }
          }
          assert(e.getSuppressed.exists(_.getMessage == "rollback failed"))
        }

        it("should verified with try boundary") {
          val t = flow { dsl =>
            dsl.withTransaction { _ => Try(42) }
          }
          assert(t.isFailure)
          val e = t.failed.get
          assert(e.getMessage == "commit failed")
          assert(e.getSuppressed.exists(_.getMessage == "rollback failed"))
        }

        it("should verified with future boundary") {
          val f = flow { dsl =>
            dsl.withTransaction { _ => Future.successful(42) }
          }
          assert(f.value.exists(_.isFailure))
          val e = f.value.get.failed.get
          assert(e.getMessage == "commit failed")
          assert(e.getSuppressed.exists(_.getMessage == "rollback failed"))
        }

      }

    }
  }


  class DummyException extends Exception("dummy") with NoStackTrace

  class DummyCommitFailedException extends RuntimeException("commit failed") with NoStackTrace

  class DummyRollbackFailedException extends RuntimeException("rollback failed") with NoStackTrace

}
