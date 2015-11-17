package jooqs.syntax

import org.jooq.impl.DSL
import org.jooq.{SQL, SQLDialect}
import org.scalatest.FunSpec

//noinspection SqlNoDataSourceInspection,SqlDialectInspection
class SQLInterpolationSpec extends FunSpec {

  describe("sql interpolation") {

    val dsl = DSL.using(SQLDialect.DEFAULT)

    it("should returns SQL object") {
      val s: SQL = sql"select 0, 1"
      assert(dsl.render(s) == "select 0, 1")
    }

    it("should bind values") {
      val s = sql"select ${0}, ${1}"
      assert(dsl.render(s) == "select ?, ?")
      assert(dsl.renderInlined(s) == "select 0, 1")
    }

    it("should bind QueryParts") {
      val s = sql"select ${DSL.value(0)}, ${DSL.value(1)}"
      assert(dsl.render(s) == "select ?, ?")
      assert(dsl.renderInlined(s) == "select 0, 1")
    }

    it("should bind inline QueryParts") {
      val s = sql"select ${DSL.inline(0)}, ${DSL.inline(1)}"
      assert(dsl.render(s) == "select 0, 1")
    }

    it("should bind values and QueryParts") {
      val s = sql"select ${0}, ${1}, ${DSL.value(0)}, ${DSL.value(1)}, ${DSL.inline(0)}, ${DSL.inline(1)}"
      assert(dsl.render(s) == "select ?, ?, ?, ?, 0, 1")
      assert(dsl.renderInlined(s) == "select 0, 1, 0, 1, 0, 1")
    }

    it("should expand sequence") {
      val s = sql"select ${Seq(0, DSL.value(1), DSL.inline(2))}"
      assert(dsl.render(s) == "select ?, ?, 2")
      assert(dsl.renderInlined(s) == "select 0, 1, 2")
    }

    it("should expand product") {
      val s = sql"insert into foo (a, b, c) values ${(0, DSL.value(1), DSL.inline(2))}"
      assert(dsl.render(s) == "insert into foo (a, b, c) values (?, ?, 2)")
      assert(dsl.renderInlined(s) == "insert into foo (a, b, c) values (0, 1, 2)")
    }

    it("should expand sequence of product") {
      val seq = Seq(
        (0, DSL.value(1), DSL.inline(2)),
        (3, DSL.value(4), DSL.inline(5)),
        (6, DSL.value(7), DSL.inline(8))
      )
      val s = sql"insert into foo (a, b, c) values $seq"
      assert(dsl.render(s) == "insert into foo (a, b, c) values (?, ?, 2), (?, ?, 5), (?, ?, 8)")
      assert(dsl.renderInlined(s) == "insert into foo (a, b, c) values (0, 1, 2), (3, 4, 5), (6, 7, 8)")
    }

    it("should expand Map") {
      val map = Map(
        DSL.inline(0) -> "foo",
        DSL.inline(1) -> "bar",
        DSL.inline(2) -> "baz"
      )
      val s = sql"insert into foo (a, b) values $map"
      assert(dsl.render(s) == "insert into foo (a, b) values (0, ?), (1, ?), (2, ?)")
      assert(dsl.renderInlined(s) == "insert into foo (a, b) values (0, 'foo'), (1, 'bar'), (2, 'baz')")
    }

  }

}
