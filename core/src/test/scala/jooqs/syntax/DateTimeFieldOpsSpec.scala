package jooqs.syntax

import jooqs.impl.IsDateTime
import org.jooq.DataType
import org.jooq.impl.{DSL, SQLDataType}
import org.jooq.types.YearToMonth
import org.scalatest.FunSpec

class DateTimeFieldOpsSpec extends FunSpec {

  describe("Date field ops") {
    it should behave like dateTimeOps(SQLDataType.DATE)
  }

  describe("Time field ops") {
    it should behave like dateTimeOps(SQLDataType.TIME)
  }

  describe("Timestamp field ops") {
    it should behave like dateTimeOps(SQLDataType.TIMESTAMP)
  }

  describe("LocalDate field ops") {
    it should behave like dateTimeOps(SQLDataType.LOCALDATE)
  }

  describe("LocalTime field ops") {
    it should behave like dateTimeOps(SQLDataType.LOCALTIME)
  }

  describe("LocalDateTime field ops") {
    it should behave like dateTimeOps(SQLDataType.LOCALDATETIME)
  }

  describe("OffsetTime field ops") {
    it should behave like dateTimeOps(SQLDataType.OFFSETTIME)
  }

  describe("OffsetDateTime field ops") {
    it should behave like dateTimeOps(SQLDataType.OFFSETDATETIME)
  }

  def dateTimeOps[A: IsDateTime](dataType: DataType[A]): Unit = {
    describe("operator '+'") {
      it("should be add with a primitive number") {
        val l = DSL.field("l", dataType)
        val r = 42
        assert(l + r == l.add(r))
      }
      it("should be add with a number") {
        val l = DSL.field("l", dataType)
        val r = Integer.valueOf(42)
        assert(l + r == l.add(r))
      }
      it("should be add with a number field") {
        val l = DSL.field("l", dataType)
        val r = DSL.field("r", SQLDataType.BIGINT)
        assert(l + r == l.add(r))
      }
      it("should be add with an interval") {
        val l = DSL.field("l", dataType)
        val r = new YearToMonth(42)
        assert(l + r == l.add(r))
      }
      it("should be add with an interval field") {
        val l = DSL.field("l", dataType)
        val r = DSL.field("r", SQLDataType.INTERVALDAYTOSECOND)
        assert(l + r == l.add(r))
      }
    }
    describe("operator '-'") {
      it("should be subtract with a primitive number") {
        val l = DSL.field("l", dataType)
        val r = 42
        assert(l - r == l.sub(r))
      }
      it("should be subtract with a number") {
        val l = DSL.field("l", dataType)
        val r = Integer.valueOf(42)
        assert(l - r == l.sub(r))
      }
      it("should be subtract with a number field") {
        val l = DSL.field("l", dataType)
        val r = DSL.field("r", SQLDataType.BIGINT)
        assert(l - r == l.sub(r))
      }
      it("should be subtract with an interval") {
        val l = DSL.field("l", dataType)
        val r = new YearToMonth(42)
        assert(l - r == l.sub(r))
      }
      it("should be subtract with an interval field") {
        val l = DSL.field("l", dataType)
        val r = DSL.field("r", SQLDataType.INTERVALDAYTOSECOND)
        assert(l - r == l.sub(r))
      }
    }
  }

}
