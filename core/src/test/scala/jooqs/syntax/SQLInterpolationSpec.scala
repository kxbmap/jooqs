package jooqs.syntax

import org.jooq.impl.DSL
import org.jooq.{SQL, SQLDialect}
import org.scalatest.FunSpec

//noinspection SqlNoDataSourceInspection
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
      val s = sql"select ${DSL.zero()}, ${DSL.one()}"
      assert(dsl.render(s) == "select ?, ?")
      assert(dsl.renderInlined(s) == "select 0, 1")
    }

    it("should bind values and QueryParts") {
      val s = sql"select ${0}, ${1}, ${DSL.zero()}, ${DSL.one()}"
      assert(dsl.render(s) == "select ?, ?, ?, ?")
      assert(dsl.renderInlined(s) == "select 0, 1, 0, 1")
    }

  }

}
