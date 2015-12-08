package jooqs.syntax

import org.jooq.impl.{DSL, SQLDataType}
import org.jooq.{Record, RecordMapper, SQLDialect}
import scalaprops.{Cogen, Gen, Properties, Scalaprops, scalazlaws}
import scalaz.std.anyVal._
import scalaz.{Unzip, Equal, Functor, Monad, Zip}


object RecordMapperOpsTest extends Scalaprops {

  val laws = {
    type F[A] = RecordMapper[Record, A]

    implicit val recordMapperInstance: Monad[F] with Zip[F] with Unzip[F] =
      new Monad[F] with Zip[F] with Unzip[F] {
        def point[A](a: => A): F[A] =
          _ => a

        def bind[A, B](fa: F[A])(f: A => F[B]): F[B] =
          fa.flatMap(f)

        override def map[A, B](fa: F[A])(f: A => B): F[B] =
          fa.fmap(f)

        def zip[A, B](a: => F[A], b: => F[B]): F[(A, B)] =
          a.zip(b)

        override def zipWith[A, B, C](fa: => F[A], fb: => F[B])(f: (A, B) => C)(implicit F: Functor[F]): F[C] =
          fa.zipWith(fb)(f)

        def unzip[A, B](a: F[(A, B)]): (F[A], F[B]) =
          a.unzip[A, B]
      }

    implicit val recordGen: Gen[Record] =
      Gen[Int].map { n =>
        DSL.using(SQLDialect.H2)
          .newRecord(DSL.field("f1", SQLDataType.INTEGER))
          .value1(n)
      }

    implicit val recordCogen: Cogen[Record] =
      Cogen[Int].contramap(_.getValue("f1", classOf[Integer]).intValue())

    implicit def recordMapperGen[A: Gen]: Gen[F[A]] =
      Gen[Record => A].map(f => f(_))

    implicit def recordMapperEqual[A: Equal]: Equal[F[A]] = {
      import FunctionEqual._
      Equal[Record => A].contramap(_.map)
    }

    Properties.list(
      scalazlaws.monad.all[F],
      scalazlaws.zip.all[F]
    )
  }

}
