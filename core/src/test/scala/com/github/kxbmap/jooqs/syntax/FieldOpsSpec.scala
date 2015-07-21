package com.github.kxbmap.jooqs.syntax

import org.jooq.impl.DSL
import org.scalatest.FunSpec

class FieldOpsSpec extends FunSpec {

  describe("FieldOps") {
    describe("operator '||'") {
      it("should be concat with a string value") {
        val l = DSL.field("l")
        val r = "foo"
        assert((l || r) == l.concat(r))
      }
      it("should be concat with a field") {
        val l = DSL.field("l")
        val r = DSL.field("r")
        assert((l || r) == l.concat(r))
      }
    }

    describe("operator '==='") {
      it("should be equal with a value") {
        val l = DSL.field("l")
        val r = "foo"
        assert((l === r) == l.equal(r))
      }
      it("should be equal with a field") {
        val l = DSL.field("l")
        val r = DSL.field("r")
        assert((l === r) == l.equal(r))
      }
      it("should be equal with a select") {
        val l = DSL.field("l")
        val r = DSL.select(DSL.field("r"))
        assert((l === r) == l.equal(r))
      }
      it("should be equal with a quantified select") {
        val l = DSL.field("l")
        val r = DSL.all(DSL.select(DSL.field("r")))
        assert((l === r) == l.equal(r))
      }
    }

    describe("operator '=!='") {
      it("should be notEqual with a value") {
        val l = DSL.field("l")
        val r = "foo"
        assert((l =!= r) == l.notEqual(r))
      }
      it("should be notEqual with a field") {
        val l = DSL.field("l")
        val r = DSL.field("r")
        assert((l =!= r) == l.notEqual(r))
      }
      it("should be notEqual with a select") {
        val l = DSL.field("l")
        val r = DSL.select(DSL.field("r"))
        assert((l =!= r) == l.notEqual(r))
      }
      it("should be notEqual with a quantified select") {
        val l = DSL.field("l")
        val r = DSL.all(DSL.select(DSL.field("r")))
        assert((l =!= r) == l.notEqual(r))
      }
    }

    describe("operator '<>'") {
      it("should be notEqual with a value") {
        val l = DSL.field("l")
        val r = "foo"
        assert((l <> r) == l.notEqual(r))
      }
      it("should be notEqual with a field") {
        val l = DSL.field("l")
        val r = DSL.field("r")
        assert((l <> r) == l.notEqual(r))
      }
      it("should be notEqual with a select") {
        val l = DSL.field("l")
        val r = DSL.select(DSL.field("r"))
        assert((l <> r) == l.notEqual(r))
      }
      it("should be notEqual with a quantified select") {
        val l = DSL.field("l")
        val r = DSL.all(DSL.select(DSL.field("r")))
        assert((l <> r) == l.notEqual(r))
      }
    }

    describe("operator '<'") {
      it("should be lessThan with a value") {
        val l = DSL.field("l")
        val r = "foo"
        assert((l < r) == l.lessThan(r))
      }
      it("should be lessThan with a field") {
        val l = DSL.field("l")
        val r = DSL.field("r")
        assert((l < r) == l.lessThan(r))
      }
      it("should be lessThan with a select") {
        val l = DSL.field("l")
        val r = DSL.select(DSL.field("r"))
        assert((l < r) == l.lessThan(r))
      }
      it("should be lessThan with a quantified select") {
        val l = DSL.field("l")
        val r = DSL.all(DSL.select(DSL.field("r")))
        assert((l < r) == l.lessThan(r))
      }
    }

    describe("operator '<='") {
      it("should be lessOrEqual with a value") {
        val l = DSL.field("l")
        val r = "foo"
        assert((l <= r) == l.lessOrEqual(r))
      }
      it("should be lessOrEqual with a field") {
        val l = DSL.field("l")
        val r = DSL.field("r")
        assert((l <= r) == l.lessOrEqual(r))
      }
      it("should be lessOrEqual with a select") {
        val l = DSL.field("l")
        val r = DSL.select(DSL.field("r"))
        assert((l <= r) == l.lessOrEqual(r))
      }
      it("should be lessOrEqual with a quantified select") {
        val l = DSL.field("l")
        val r = DSL.all(DSL.select(DSL.field("r")))
        assert((l <= r) == l.lessOrEqual(r))
      }
    }

    describe("operator '>'") {
      it("should be greaterThan with a value") {
        val l = DSL.field("l")
        val r = "foo"
        assert((l > r) == l.greaterThan(r))
      }
      it("should be greaterThan with a field") {
        val l = DSL.field("l")
        val r = DSL.field("r")
        assert((l > r) == l.greaterThan(r))
      }
      it("should be greaterThan with a select") {
        val l = DSL.field("l")
        val r = DSL.select(DSL.field("r"))
        assert((l > r) == l.greaterThan(r))
      }
      it("should be greaterThan with a quantified select") {
        val l = DSL.field("l")
        val r = DSL.all(DSL.select(DSL.field("r")))
        assert((l > r) == l.greaterThan(r))
      }
    }

    describe("operator '>='") {
      it("should be greaterOrEqual with a value") {
        val l = DSL.field("l")
        val r = "foo"
        assert((l >= r) == l.greaterOrEqual(r))
      }
      it("should be greaterOrEqual with a field") {
        val l = DSL.field("l")
        val r = DSL.field("r")
        assert((l >= r) == l.greaterOrEqual(r))
      }
      it("should be greaterOrEqual with a select") {
        val l = DSL.field("l")
        val r = DSL.select(DSL.field("r"))
        assert((l >= r) == l.greaterOrEqual(r))
      }
      it("should be greaterOrEqual with a quantified select") {
        val l = DSL.field("l")
        val r = DSL.all(DSL.select(DSL.field("r")))
        assert((l >= r) == l.greaterOrEqual(r))
      }
    }

    describe("operator '<=>'") {
      it("should be isNotDistinctFrom with a value") {
        val l = DSL.field("l")
        val r = "foo"
        assert((l <=> r) == l.isNotDistinctFrom(r))
      }
      it("should be isNotDistinctFrom with a field") {
        val l = DSL.field("l")
        val r = DSL.field("r")
        assert((l <=> r) == l.isNotDistinctFrom(r))
      }
    }
  }
}
