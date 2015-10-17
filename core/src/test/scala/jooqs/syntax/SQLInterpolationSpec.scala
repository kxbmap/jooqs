package jooqs.syntax

import org.jooq.impl.DSL
import org.jooq.{SQL, SQLDialect}
import org.scalatest.FunSpec

//noinspection SqlNoDataSourceInspection
class SQLInterpolationSpec extends FunSpec {

  describe("sql interpolation") {

    val dsl = DSL.using(SQLDialect.DEFAULT)

    it("should returns SQL object") {
      val s: SQL = sql"select 1"
      assert(dsl.render(s) == "select 1")
    }

    it("should bind value") {
      val n = 1
      val s = sql"select $n"
      assert(dsl.render(s) == "select ?")
      assert(dsl.renderInlined(s) == "select 1")
    }

    it("should bind QueryPart") {
      val p = DSL.one()
      val s = sql"select $p"
      assert(dsl.render(s) == "select ?")
      assert(dsl.renderInlined(s) == "select 1")
    }

  }

}
