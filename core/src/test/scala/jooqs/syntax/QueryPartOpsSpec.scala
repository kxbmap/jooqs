package jooqs.syntax

import org.jooq.impl.DSL
import org.scalatest.FunSpec

class QueryPartOpsSpec extends FunSpec {

  describe("QueryPartOps") {
    describe("map") {
      it("should map a QueryPart") {
        assert(DSL.value(42).map(_.getName) == "42")
      }
    }

    describe("mapIf") {
      it("should map a QueryPart if condition is true") {
        val part = DSL.value(42).mapIf(cond = true, _.plus(1))
        assert(part == DSL.value(42).plus(1))
      }

      it("should do nothing if condition is false") {
        val part = DSL.value(42).mapIf(cond = false, _.plus(1))
        assert(part == DSL.value(42))
      }
    }
  }
}
