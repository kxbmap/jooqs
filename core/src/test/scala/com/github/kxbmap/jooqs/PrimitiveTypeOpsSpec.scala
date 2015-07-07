package com.github.kxbmap.jooqs

import org.scalatest.FunSpec

//noinspection OptionEqualsSome,EmptyCheck
class PrimitiveTypeOpsSpec extends FunSpec with DisableAutoboxing {

  import syntax._

  describe("PrimitiveTypeOps") {
    describe("boxed") {
      it("should return boxed value") {
        val result: java.lang.Long = 42L.boxed
        assert(result == 42L)
      }
    }
  }

  describe("PrimitiveTypeOptionOps") {
    describe("boxed") {
      it("should return boxed option value") {
        val result: Option[java.lang.Long] = Some(42L).boxed
        assert(result == Some(42L))
      }

      it("should return None if None") {
        assert((None: Option[Long]).boxed == None)
      }
    }
  }

}
