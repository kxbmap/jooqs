package com.github.kxbmap.jooqs.syntax

import com.github.kxbmap.jooqs.DisableAutoUnboxing
import org.jooq.Record
import org.jooq.impl.DSL
import org.mockito.Mockito._
import org.scalatest.FunSpec
import org.scalatest.mock.MockitoSugar

//noinspection OptionEqualsSome,EmptyCheck
class RecordOpsSpec extends FunSpec with MockitoSugar with DisableAutoUnboxing {

  describe("RecordOps") {
    describe("get") {
      describe("boxed type field") {
        it("should return unboxed value") {
          val record = mock[Record]
          val field = DSL.field("f", classOf[java.lang.Long])

          when(record.getValue(field)).thenReturn(42L)

          val result: Long = record.get(field)
          assert(result == 42L)
        }

        it("should throw NPE when field is null") {
          val record = mock[Record]
          val field = DSL.field("f", classOf[java.lang.Long])

          when(record.getValue(field)).thenReturn(null)

          intercept[NullPointerException] {
            record.get(field)
          }
        }
      }

      describe("other type field") {
        it("should return value") {
          val record = mock[Record]
          val field = DSL.field("f", classOf[String])

          when(record.getValue(field)).thenReturn("foo")

          val result: String = record.get(field)
          assert(result == "foo")
        }

        it("should return null when field is null") {
          val record = mock[Record]
          val field = DSL.field("f", classOf[String])

          when(record.getValue(field)).thenReturn(null)

          val result: String = record.get(field)
          assert(result == null)
        }
      }
    }

    describe("getOpt") {
      describe("boxed type field") {
        it("should return unboxed option value") {
          val record = mock[Record]
          val field = DSL.field("f", classOf[java.lang.Long])

          when(record.getValue(field)).thenReturn(42L)

          val result: Option[Long] = record.getOpt(field)
          assert(result == Some(42L))
        }

        it("should return None when field is null") {
          val record = mock[Record]
          val field = DSL.field("f", classOf[java.lang.Long])

          when(record.getValue(field)).thenReturn(null)

          assert(record.getOpt(field) == None)
        }
      }

      describe("other type field") {
        it("should return option value") {
          val record = mock[Record]
          val field = DSL.field("f", classOf[String])

          when(record.getValue(field)).thenReturn("foo")

          val result: Option[String] = record.getOpt(field)
          assert(result == Some("foo"))
        }

        it("should return None when field is null") {
          val record = mock[Record]
          val field = DSL.field("f", classOf[String])

          when(record.getValue(field)).thenReturn(null)

          assert(record.getOpt(field) == None)
        }
      }
    }
  }

}
