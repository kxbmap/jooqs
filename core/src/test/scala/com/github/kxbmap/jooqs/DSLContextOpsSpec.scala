package com.github.kxbmap.jooqs

import org.jooq._
import org.jooq.impl.DSL
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.mockito.{Mockito => M}
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


      def verifyFlow[T](tx: DSLContext => T)(
        when: TransactionProvider => Unit = (_: TransactionProvider) => (),
        verify: TransactionProvider => Unit = (_: TransactionProvider) => ())
        (implicit await: T => Unit = (_: T) => ()): T = {

        val dsl = mock[DSLContext](RETURNS_DEEP_STUBS)
        val provider = mock[TransactionProvider]

        M.when(dsl.configuration().derive().transactionProvider()).thenReturn(provider)
        when(provider)

        val result = tx(dsl)
        await(result)

        verify(provider)
        verifyNoMoreInteractions(provider)

        result
      }


      describe("flow of commit") {

        def verifyCommitFlow[T](tx: DSLContext => T)(implicit await: T => Unit = (_: T) => ()): T = verifyFlow(tx)(
          verify = { provider =>
            val o = inOrder(provider)
            o.verify(provider).begin(any())
            o.verify(provider).commit(any())
          }
        )

        it("should verified with exception boundary") {
          val r = verifyCommitFlow { dsl =>
            dsl.withTransaction { _ => 42 }
          }
          assert(r == 42)
        }

        it("should verified with try boundary") {
          val r = verifyCommitFlow { dsl =>
            dsl.withTransaction { _ => Try(42) }
          }
          assert(r.isSuccess)
        }

        it("should verified with future boundary") {
          val r = verifyCommitFlow { dsl =>
            dsl.withTransaction { _ => Future.successful(42) }
          }
          assert(r.value.exists(_.isSuccess))
        }

      }

      describe("flow of rollback") {

        def verifyRollbackFlow[T](tx: DSLContext => T)(implicit await: T => Unit = (_: T) => ()): T = verifyFlow(tx)(
          verify = { provider =>
            val o = inOrder(provider)
            o.verify(provider).begin(any())
            o.verify(provider).rollback(any())
          }
        )

        it("should verified with exception boundary") {
          verifyRollbackFlow { dsl =>
            intercept[DummyException] {
              dsl.withTransaction[Int] { _ => throw new DummyException }
            }
          }
        }

        it("should verified with try boundary") {
          val r = verifyRollbackFlow { dsl =>
            dsl.withTransaction { _ => Try[Int] { throw new DummyException } }
          }
          assert(r.isFailure)
          assert(r.failed.get.getMessage == "dummy")
        }

        it("should verified with future boundary") {
          val r = verifyRollbackFlow { dsl =>
            dsl.withTransaction { _ => Future.failed[Int](new DummyException) }
          }
          assert(r.value.exists(_.isFailure))
          assert(r.value.get.failed.get.getMessage == "dummy")
        }

        describe("with uncaught exception") {

          it("should verified with try boundary") {
            verifyRollbackFlow { dsl =>
              intercept[DummyException] {
                dsl.withTransaction { _ =>
                  throw new DummyException
                  Try(42)
                }
              }
            }
          }

          it("should verified with future boundary") {
            verifyRollbackFlow { dsl =>
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

        def verifyCommitFailFlow[T](tx: DSLContext => T)(implicit await: T => Unit = (_: T) => ()): T = verifyFlow(tx)(
          when = { provider =>
            when(provider.commit(any())).thenThrow(new DummyCommitFailedException)
          },
          verify = { provider =>
            val o = inOrder(provider)
            o.verify(provider).begin(any())
            o.verify(provider).commit(any())
            o.verify(provider).rollback(any())
          }
        )

        it("should verified with exception boundary") {
          verifyCommitFailFlow { dsl =>
            intercept[DummyCommitFailedException] {
              dsl.withTransaction { _ => 42 }
            }
          }
        }

        it("should verified with try boundary") {
          val t = verifyCommitFailFlow { dsl =>
            dsl.withTransaction { _ => Try(42) }
          }
          assert(t.isFailure)
          assert(t.failed.get.getMessage == "commit failed")
        }

        it("should verified with future boundary") {
          val f = verifyCommitFailFlow { dsl =>
            dsl.withTransaction { _ => Future.successful(42) }
          }
          assert(f.value.exists(_.isFailure))
          assert(f.value.get.failed.get.getMessage == "commit failed")
        }

      }

      describe("flow of rollback to fail") {

        def verifyRollbackFailFlow[T](tx: DSLContext => T)(implicit await: T => Unit = (_: T) => ()): T = verifyFlow(tx)(
          when = { provider =>
            when(provider.rollback(any())).thenThrow(new DummyRollbackFailedException)
          },
          verify = { provider =>
            val o = inOrder(provider)
            o.verify(provider).begin(any())
            o.verify(provider).rollback(any())
          }
        )

        it("should verified with exception boundary") {
          val e = verifyRollbackFailFlow { dsl =>
            intercept[DummyRollbackFailedException] {
              dsl.withTransaction[Int] { _ =>
                throw new DummyException
              }
            }
          }
          assert(e.getSuppressed.exists(_.getMessage == "dummy"))
        }

        it("should verified with try boundary") {
          val t = verifyRollbackFailFlow { dsl =>
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
          val f = verifyRollbackFailFlow { dsl =>
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
            val e = verifyRollbackFailFlow { dsl =>
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
            val e = verifyRollbackFailFlow { dsl =>
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

        def verifyCommitAndRollbackFailFlow[T](tx: DSLContext => T)(implicit await: T => Unit = (_: T) => ()): T = verifyFlow(tx)(
          when = { provider =>
            when(provider.commit(any())).thenThrow(new DummyCommitFailedException)
            when(provider.rollback(any())).thenThrow(new DummyRollbackFailedException)
          },
          verify = { provider =>
            val o = inOrder(provider)
            o.verify(provider).begin(any())
            o.verify(provider).commit(any())
            o.verify(provider).rollback(any())
          }
        )

        it("should verified with exception boundary") {
          val e = verifyCommitAndRollbackFailFlow { dsl =>
            intercept[DummyCommitFailedException] {
              dsl.withTransaction { _ => 42 }
            }
          }
          assert(e.getSuppressed.exists(_.getMessage == "rollback failed"))
        }

        it("should verified with try boundary") {
          val t = verifyCommitAndRollbackFailFlow { dsl =>
            dsl.withTransaction { _ => Try(42) }
          }
          assert(t.isFailure)
          val e = t.failed.get
          assert(e.getMessage == "commit failed")
          assert(e.getSuppressed.exists(_.getMessage == "rollback failed"))
        }

        it("should verified with future boundary") {
          val f = verifyCommitAndRollbackFailFlow { dsl =>
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
