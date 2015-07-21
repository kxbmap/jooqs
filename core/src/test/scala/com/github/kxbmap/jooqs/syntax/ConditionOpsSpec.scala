package com.github.kxbmap.jooqs.syntax

import org.jooq.impl.{DSL, SQLDataType}
import org.scalatest.FunSpec

class ConditionOpsSpec extends FunSpec {

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
      it("should be 'and' with inlined boolean") {
        val l = DSL.trueCondition()
        val r: java.lang.Boolean = true
        assert((l && r) == l.and(DSL.inline(r)))
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
      it("should be 'or' with inlined boolean") {
        val l = DSL.trueCondition()
        val r: java.lang.Boolean = true
        assert((l || r) == l.or(DSL.inline(r)))
      }
    }

  }
}
