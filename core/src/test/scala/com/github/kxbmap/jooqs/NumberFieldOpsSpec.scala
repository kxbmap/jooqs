package com.github.kxbmap.jooqs

import org.jooq.impl.{SQLDataType, DSL}
import org.scalatest.FunSpec

class NumberFieldOpsSpec extends FunSpec {

  import syntax._

  describe("NumberFieldOps") {
    describe("unary operator '-'") {
      it("should be negate") {
        val f = DSL.field("f", SQLDataType.BIGINT)
        assert(-f == f.neg())
      }
    }

    describe("operator '+'") {
      it("should be add with a number") {
        val l = DSL.field("l", SQLDataType.BIGINT)
        val r = 42
        assert(l + r == l.add(r))
      }
      it("should be add with a field") {
        val l = DSL.field("l", SQLDataType.BIGINT)
        val r = DSL.field("r", SQLDataType.BIGINT)
        assert(l + r == l.add(r))
      }
    }

    describe("operator '-'") {
      it("should be subtract with a number") {
        val l = DSL.field("l", SQLDataType.BIGINT)
        val r = 42
        assert(l - r == l.sub(r))
      }
      it("should be subtract with a field") {
        val l = DSL.field("l", SQLDataType.BIGINT)
        val r = DSL.field("r", SQLDataType.BIGINT)
        assert(l - r == l.sub(r))
      }
    }

    describe("operator '*'") {
      it("should be multiply with a number") {
        val l = DSL.field("l", SQLDataType.BIGINT)
        val r = 42
        assert(l * r == l.mul(r))
      }
      it("should be multiply with a field") {
        val l = DSL.field("l", SQLDataType.BIGINT)
        val r = DSL.field("r", SQLDataType.BIGINT)
        assert(l * r == l.mul(r))
      }
    }

    describe("operator '/'") {
      it("should be divide with a number") {
        val l = DSL.field("l", SQLDataType.BIGINT)
        val r = 42
        assert(l / r == l.div(r))
      }
      it("should be divide with a field") {
        val l = DSL.field("l", SQLDataType.BIGINT)
        val r = DSL.field("r", SQLDataType.BIGINT)
        assert(l / r == l.div(r))
      }
    }

    describe("operator '%'") {
      it("should be modulo with a number") {
        val l = DSL.field("l", SQLDataType.BIGINT)
        val r = 42
        assert(l % r == l.mod(r))
      }
      it("should be modulo with a field") {
        val l = DSL.field("l", SQLDataType.BIGINT)
        val r = DSL.field("r", SQLDataType.BIGINT)
        assert(l % r == l.mod(r))
      }
    }

    describe("unary operator '~'") {
      it("should be bitNot") {
        val f = DSL.field("f", SQLDataType.BIGINT)
        assert(~f == f.bitNot())
      }
    }

    describe("operator '&'") {
      it("should be bitAnd with a number") {
        val l = DSL.field("l", SQLDataType.BIGINT)
        val r = 42L
        assert((l & r) == l.bitAnd(r))
      }
      it("should be bitAnd with a field") {
        val l = DSL.field("l", SQLDataType.BIGINT)
        val r = DSL.field("r", SQLDataType.BIGINT)
        assert((l & r) == l.bitAnd(r))
      }
    }

    describe("operator '|'") {
      it("should be bitOr with a number") {
        val l = DSL.field("l", SQLDataType.BIGINT)
        val r = 42L
        assert((l | r) == l.bitOr(r))
      }
      it("should be bitOr with a field") {
        val l = DSL.field("l", SQLDataType.BIGINT)
        val r = DSL.field("r", SQLDataType.BIGINT)
        assert((l | r) == l.bitOr(r))
      }
    }

    describe("operator '^'") {
      it("should be bitXor with a number") {
        val l = DSL.field("l", SQLDataType.BIGINT)
        val r = 42L
        assert((l ^ r) == l.bitXor(r))
      }
      it("should be bitXor with a field") {
        val l = DSL.field("l", SQLDataType.BIGINT)
        val r = DSL.field("r", SQLDataType.BIGINT)
        assert((l ^ r) == l.bitXor(r))
      }
    }

    describe("operator '~&'") {
      it("should be bitNand with a number") {
        val l = DSL.field("l", SQLDataType.BIGINT)
        val r = 42L
        assert((l ~& r) == l.bitNand(r))
      }
      it("should be bitNand with a field") {
        val l = DSL.field("l", SQLDataType.BIGINT)
        val r = DSL.field("r", SQLDataType.BIGINT)
        assert((l ~& r) == l.bitNand(r))
      }
    }

    describe("operator '~|'") {
      it("should be bitNor with a number") {
        val l = DSL.field("l", SQLDataType.BIGINT)
        val r = 42L
        assert((l ~| r) == l.bitNor(r))
      }
      it("should be bitNor with a field") {
        val l = DSL.field("l", SQLDataType.BIGINT)
        val r = DSL.field("r", SQLDataType.BIGINT)
        assert((l ~| r) == l.bitNor(r))
      }
    }

    describe("operator '~^'") {
      it("should be bitXNor with a number") {
        val l = DSL.field("l", SQLDataType.BIGINT)
        val r = 42L
        assert((l ~^ r) == l.bitXNor(r))
      }
      it("should be bitXNor with a field") {
        val l = DSL.field("l", SQLDataType.BIGINT)
        val r = DSL.field("r", SQLDataType.BIGINT)
        assert((l ~^ r) == l.bitXNor(r))
      }
    }

    describe("operator '<<'") {
      it("should be left shift with a number") {
        val l = DSL.field("l", SQLDataType.BIGINT)
        val r = 42L
        assert((l << r) == l.shl(r))
      }
      it("should be left shift with a field") {
        val l = DSL.field("l", SQLDataType.BIGINT)
        val r = DSL.field("r", SQLDataType.BIGINT)
        assert((l << r) == l.shl(r))
      }
    }

    describe("operator '>>'") {
      it("should be right shift with a number") {
        val l = DSL.field("l", SQLDataType.BIGINT)
        val r = 42L
        assert((l >> r) == l.shr(r))
      }
      it("should be right shift with a field") {
        val l = DSL.field("l", SQLDataType.BIGINT)
        val r = DSL.field("r", SQLDataType.BIGINT)
        assert((l >> r) == l.shr(r))
      }
    }

  }
}
