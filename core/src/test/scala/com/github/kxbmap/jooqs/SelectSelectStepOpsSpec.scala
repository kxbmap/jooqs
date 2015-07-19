package com.github.kxbmap.jooqs

import org.jooq.impl.DSL
import org.jooq.{Field, SQLDialect}
import org.scalatest.FunSpec

class SelectSelectStepOpsSpec extends FunSpec {

  import syntax._

  val dsl = DSL.using(SQLDialect.DEFAULT)

  describe("SelectSelectStepOps") {

    describe("select") {
      it("should accept Array[Field[_]]") {
        val fields = Array[Field[_]](DSL.field("f1"), DSL.field("f2"), DSL.field("f3"))
        assert(dsl.select().select(fields) == dsl.select().select(fields: _*))
      }

      it("should accept Seq[Field[_]]") {
        val fields = Seq(DSL.field("f1"), DSL.field("f2"), DSL.field("f3"))
        assert(dsl.select().select(fields) == dsl.select().select(fields: _*))
      }
    }
  }

}
