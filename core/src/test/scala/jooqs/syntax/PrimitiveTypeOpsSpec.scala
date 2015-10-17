package jooqs.syntax

import jooqs.DisableAutoboxing
import org.scalatest.FunSpec

//noinspection OptionEqualsSome,EmptyCheck
class PrimitiveTypeOpsSpec extends FunSpec with DisableAutoboxing {

  describe("PrimitiveTypeOps") {
    describe("box") {
      it("should return boxed value") {
        val result: java.lang.Long = 42L.box
        assert(result == 42L)
      }
    }
  }

  describe("PrimitiveTypeOptionOps") {
    describe("box") {
      it("should return boxed option value") {
        val result: Option[java.lang.Long] = Some(42L).box
        assert(result == Some(42L))
      }

      it("should return None if value is None") {
        assert((None: Option[Long]).box == None)
      }
    }
  }

}
