package com.github.kxbmap.jooqs

import org.jooq.impl.{DSL, SQLDataType}
import org.scalatest.FunSpec

class ConditionOpsSpec extends FunSpec {

  import syntax._

  describe("ConditionOps") {
    describe("unary operator '!'") {
      it("should be 'not'") {
        val c = DSL.trueCondition()
        assert(!c == c.not())
      }
    }

    describe("operator '&&'") {
      it("should be 'and' with condition") {
        val l = DSL.trueCondition()
        val r = DSL.falseCondition()
        assert((l && r) == l.and(r))
      }
      it("should be 'and' with boolean field") {
        val l = DSL.trueCondition()
        val r = DSL.field("r", SQLDataType.BOOLEAN)
        assert((l && r) == l.and(r))
      }
      it("should be 'and' with boolean as value") {
        val l = DSL.trueCondition()
        val r: java.lang.Boolean = true
        assert((l && r) == l.and(DSL.value(r)))
      }
    }

    describe("operator '||'") {
      it("should be 'or' with condition") {
        val l = DSL.trueCondition()
        val r = DSL.falseCondition()
        assert((l || r) == l.or(r))
      }
      it("should be 'or' with boolean field") {
        val l = DSL.trueCondition()
        val r = DSL.field("r", SQLDataType.BOOLEAN)
        assert((l || r) == l.or(r))
      }
      it("should be 'or' with boolean as value") {
        val l = DSL.trueCondition()
        val r: java.lang.Boolean = true
        assert((l || r) == l.or(DSL.value(r)))
      }
    }

  }
}
