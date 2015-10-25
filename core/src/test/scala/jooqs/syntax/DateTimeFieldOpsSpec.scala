package jooqs.syntax

import org.jooq.impl.{DSL, SQLDataType}
import org.scalatest.FunSpec

class DateTimeFieldOpsSpec extends FunSpec {

  describe("DateFieldOps") {
    describe("operator '+'") {
      it("should be add with a number") {
        val l = DSL.field("l", SQLDataType.DATE)
        val r = 42
        assert(l + r == l.add(r))
      }
      it("should be add with a field") {
        val l = DSL.field("l", SQLDataType.DATE)
        val r = DSL.field("r", SQLDataType.BIGINT)
        assert(l + r == l.add(r))
      }
    }
    describe("operator '-'") {
      it("should be subtract with a number") {
        val l = DSL.field("l", SQLDataType.DATE)
        val r = 42
        assert(l - r == l.subtract(r))
      }
      it("should be subtract with a field") {
        val l = DSL.field("l", SQLDataType.DATE)
        val r = DSL.field("r", SQLDataType.BIGINT)
        assert(l - r == l.subtract(r))
      }
    }
  }

  describe("TimeFieldOps") {
    describe("operator '+'") {
      it("should be add with a number") {
        val l = DSL.field("l", SQLDataType.TIME)
        val r = 42
        assert(l + r == l.add(r))
      }
      it("should be add with a field") {
        val l = DSL.field("l", SQLDataType.TIME)
        val r = DSL.field("r", SQLDataType.BIGINT)
        assert(l + r == l.add(r))
      }
    }
    describe("operator '-'") {
      it("should be subtract with a number") {
        val l = DSL.field("l", SQLDataType.TIME)
        val r = 42
        assert(l - r == l.subtract(r))
      }
      it("should be subtract with a field") {
        val l = DSL.field("l", SQLDataType.TIME)
        val r = DSL.field("r", SQLDataType.BIGINT)
        assert(l - r == l.subtract(r))
      }
    }
  }

  describe("TimestampFieldOps") {
    describe("operator '+'") {
      it("should be add with a number") {
        val l = DSL.field("l", SQLDataType.TIMESTAMP)
        val r = 42
        assert(l + r == l.add(r))
      }
      it("should be add with a field") {
        val l = DSL.field("l", SQLDataType.TIMESTAMP)
        val r = DSL.field("r", SQLDataType.BIGINT)
        assert(l + r == l.add(r))
      }
    }
    describe("operator '-'") {
      it("should be subtract with a number") {
        val l = DSL.field("l", SQLDataType.TIMESTAMP)
        val r = 42
        assert(l - r == l.subtract(r))
      }
      it("should be subtract with a field") {
        val l = DSL.field("l", SQLDataType.TIMESTAMP)
        val r = DSL.field("r", SQLDataType.BIGINT)
        assert(l - r == l.subtract(r))
      }
    }
  }

}
