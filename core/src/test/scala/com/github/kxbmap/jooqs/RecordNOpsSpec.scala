package com.github.kxbmap.jooqs

import java.sql.DriverManager
import org.jooq.impl.DSL
import org.scalatest.{BeforeAndAfterAll, FunSpec}

class RecordNOpsSpec extends FunSpec with BeforeAndAfterAll {

  import syntax._

  val conn = {
    Class.forName("org.h2.Driver")
    DriverManager.getConnection("jdbc:h2:mem:RecordNOpsSpec")
  }

  val dsl = DSL.using(conn)

  override protected def afterAll(): Unit = {
    conn.close()
  }

  describe("Record1Ops") {
    describe("asTuple") {
      it("should convert to tuple") {
        val r = dsl.select(
          DSL.inline(1)
        ).fetchOne()

        assert(r.asTuple == Tuple1(1))
      }
    }
  }

  describe("Record2Ops") {
    describe("asTuple") {
      it("should convert to tuple") {
        val r = dsl.select(
          DSL.inline(1),
          DSL.inline(2)
        ).fetchOne()

        assert(r.asTuple == ((1, 2)))
      }
    }
  }

  describe("Record3Ops") {
    describe("asTuple") {
      it("should convert to tuple") {
        val r = dsl.select(
          DSL.inline(1),
          DSL.inline(2),
          DSL.inline(3)
        ).fetchOne()

        assert(r.asTuple == ((1, 2, 3)))
      }
    }
  }

  describe("Record4Ops") {
    describe("asTuple") {
      it("should convert to tuple") {
        val r = dsl.select(
          DSL.inline(1),
          DSL.inline(2),
          DSL.inline(3),
          DSL.inline(4)
        ).fetchOne()

        assert(r.asTuple == ((1, 2, 3, 4)))
      }
    }
  }

  describe("Record5Ops") {
    describe("asTuple") {
      it("should convert to tuple") {
        val r = dsl.select(
          DSL.inline(1),
          DSL.inline(2),
          DSL.inline(3),
          DSL.inline(4),
          DSL.inline(5)
        ).fetchOne()

        assert(r.asTuple == ((1, 2, 3, 4, 5)))
      }
    }
  }

  describe("Record6Ops") {
    describe("asTuple") {
      it("should convert to tuple") {
        val r = dsl.select(
          DSL.inline(1),
          DSL.inline(2),
          DSL.inline(3),
          DSL.inline(4),
          DSL.inline(5),
          DSL.inline(6)
        ).fetchOne()

        assert(r.asTuple == ((1, 2, 3, 4, 5, 6)))
      }
    }
  }

  describe("Record7Ops") {
    describe("asTuple") {
      it("should convert to tuple") {
        val r = dsl.select(
          DSL.inline(1),
          DSL.inline(2),
          DSL.inline(3),
          DSL.inline(4),
          DSL.inline(5),
          DSL.inline(6),
          DSL.inline(7)
        ).fetchOne()

        assert(r.asTuple == ((1, 2, 3, 4, 5, 6, 7)))
      }
    }
  }

  describe("Record8Ops") {
    describe("asTuple") {
      it("should convert to tuple") {
        val r = dsl.select(
          DSL.inline(1),
          DSL.inline(2),
          DSL.inline(3),
          DSL.inline(4),
          DSL.inline(5),
          DSL.inline(6),
          DSL.inline(7),
          DSL.inline(8)
        ).fetchOne()

        assert(r.asTuple == ((1, 2, 3, 4, 5, 6, 7, 8)))
      }
    }
  }

  describe("Record9Ops") {
    describe("asTuple") {
      it("should convert to tuple") {
        val r = dsl.select(
          DSL.inline(1),
          DSL.inline(2),
          DSL.inline(3),
          DSL.inline(4),
          DSL.inline(5),
          DSL.inline(6),
          DSL.inline(7),
          DSL.inline(8),
          DSL.inline(9)
        ).fetchOne()

        assert(r.asTuple == ((1, 2, 3, 4, 5, 6, 7, 8, 9)))
      }
    }
  }

  describe("Record10Ops") {
    describe("asTuple") {
      it("should convert to tuple") {
        val r = dsl.select(
          DSL.inline(1),
          DSL.inline(2),
          DSL.inline(3),
          DSL.inline(4),
          DSL.inline(5),
          DSL.inline(6),
          DSL.inline(7),
          DSL.inline(8),
          DSL.inline(9),
          DSL.inline(10)
        ).fetchOne()

        assert(r.asTuple == ((1, 2, 3, 4, 5, 6, 7, 8, 9, 10)))
      }
    }
  }

  describe("Record11Ops") {
    describe("asTuple") {
      it("should convert to tuple") {
        val r = dsl.select(
          DSL.inline(1),
          DSL.inline(2),
          DSL.inline(3),
          DSL.inline(4),
          DSL.inline(5),
          DSL.inline(6),
          DSL.inline(7),
          DSL.inline(8),
          DSL.inline(9),
          DSL.inline(10),
          DSL.inline(11)
        ).fetchOne()

        assert(r.asTuple == ((1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11)))
      }
    }
  }

  describe("Record12Ops") {
    describe("asTuple") {
      it("should convert to tuple") {
        val r = dsl.select(
          DSL.inline(1),
          DSL.inline(2),
          DSL.inline(3),
          DSL.inline(4),
          DSL.inline(5),
          DSL.inline(6),
          DSL.inline(7),
          DSL.inline(8),
          DSL.inline(9),
          DSL.inline(10),
          DSL.inline(11),
          DSL.inline(12)
        ).fetchOne()

        assert(r.asTuple == ((1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)))
      }
    }
  }

  describe("Record13Ops") {
    describe("asTuple") {
      it("should convert to tuple") {
        val r = dsl.select(
          DSL.inline(1),
          DSL.inline(2),
          DSL.inline(3),
          DSL.inline(4),
          DSL.inline(5),
          DSL.inline(6),
          DSL.inline(7),
          DSL.inline(8),
          DSL.inline(9),
          DSL.inline(10),
          DSL.inline(11),
          DSL.inline(12),
          DSL.inline(13)
        ).fetchOne()

        assert(r.asTuple == ((1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13)))
      }
    }
  }

  describe("Record14Ops") {
    describe("asTuple") {
      it("should convert to tuple") {
        val r = dsl.select(
          DSL.inline(1),
          DSL.inline(2),
          DSL.inline(3),
          DSL.inline(4),
          DSL.inline(5),
          DSL.inline(6),
          DSL.inline(7),
          DSL.inline(8),
          DSL.inline(9),
          DSL.inline(10),
          DSL.inline(11),
          DSL.inline(12),
          DSL.inline(13),
          DSL.inline(14)
        ).fetchOne()

        assert(r.asTuple == ((1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14)))
      }
    }
  }

  describe("Record15Ops") {
    describe("asTuple") {
      it("should convert to tuple") {
        val r = dsl.select(
          DSL.inline(1),
          DSL.inline(2),
          DSL.inline(3),
          DSL.inline(4),
          DSL.inline(5),
          DSL.inline(6),
          DSL.inline(7),
          DSL.inline(8),
          DSL.inline(9),
          DSL.inline(10),
          DSL.inline(11),
          DSL.inline(12),
          DSL.inline(13),
          DSL.inline(14),
          DSL.inline(15)
        ).fetchOne()

        assert(r.asTuple == ((1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)))
      }
    }
  }

  describe("Record16Ops") {
    describe("asTuple") {
      it("should convert to tuple") {
        val r = dsl.select(
          DSL.inline(1),
          DSL.inline(2),
          DSL.inline(3),
          DSL.inline(4),
          DSL.inline(5),
          DSL.inline(6),
          DSL.inline(7),
          DSL.inline(8),
          DSL.inline(9),
          DSL.inline(10),
          DSL.inline(11),
          DSL.inline(12),
          DSL.inline(13),
          DSL.inline(14),
          DSL.inline(15),
          DSL.inline(16)
        ).fetchOne()

        assert(r.asTuple == ((1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16)))
      }
    }
  }

  describe("Record17Ops") {
    describe("asTuple") {
      it("should convert to tuple") {
        val r = dsl.select(
          DSL.inline(1),
          DSL.inline(2),
          DSL.inline(3),
          DSL.inline(4),
          DSL.inline(5),
          DSL.inline(6),
          DSL.inline(7),
          DSL.inline(8),
          DSL.inline(9),
          DSL.inline(10),
          DSL.inline(11),
          DSL.inline(12),
          DSL.inline(13),
          DSL.inline(14),
          DSL.inline(15),
          DSL.inline(16),
          DSL.inline(17)
        ).fetchOne()

        assert(r.asTuple == ((1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17)))
      }
    }
  }

  describe("Record18Ops") {
    describe("asTuple") {
      it("should convert to tuple") {
        val r = dsl.select(
          DSL.inline(1),
          DSL.inline(2),
          DSL.inline(3),
          DSL.inline(4),
          DSL.inline(5),
          DSL.inline(6),
          DSL.inline(7),
          DSL.inline(8),
          DSL.inline(9),
          DSL.inline(10),
          DSL.inline(11),
          DSL.inline(12),
          DSL.inline(13),
          DSL.inline(14),
          DSL.inline(15),
          DSL.inline(16),
          DSL.inline(17),
          DSL.inline(18)
        ).fetchOne()

        assert(r.asTuple == ((1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18)))
      }
    }
  }

  describe("Record19Ops") {
    describe("asTuple") {
      it("should convert to tuple") {
        val r = dsl.select(
          DSL.inline(1),
          DSL.inline(2),
          DSL.inline(3),
          DSL.inline(4),
          DSL.inline(5),
          DSL.inline(6),
          DSL.inline(7),
          DSL.inline(8),
          DSL.inline(9),
          DSL.inline(10),
          DSL.inline(11),
          DSL.inline(12),
          DSL.inline(13),
          DSL.inline(14),
          DSL.inline(15),
          DSL.inline(16),
          DSL.inline(17),
          DSL.inline(18),
          DSL.inline(19)
        ).fetchOne()

        assert(r.asTuple == ((1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19)))
      }
    }
  }

  describe("Record20Ops") {
    describe("asTuple") {
      it("should convert to tuple") {
        val r = dsl.select(
          DSL.inline(1),
          DSL.inline(2),
          DSL.inline(3),
          DSL.inline(4),
          DSL.inline(5),
          DSL.inline(6),
          DSL.inline(7),
          DSL.inline(8),
          DSL.inline(9),
          DSL.inline(10),
          DSL.inline(11),
          DSL.inline(12),
          DSL.inline(13),
          DSL.inline(14),
          DSL.inline(15),
          DSL.inline(16),
          DSL.inline(17),
          DSL.inline(18),
          DSL.inline(19),
          DSL.inline(20)
        ).fetchOne()

        assert(r.asTuple == ((1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20)))
      }
    }
  }

  describe("Record21Ops") {
    describe("asTuple") {
      it("should convert to tuple") {
        val r = dsl.select(
          DSL.inline(1),
          DSL.inline(2),
          DSL.inline(3),
          DSL.inline(4),
          DSL.inline(5),
          DSL.inline(6),
          DSL.inline(7),
          DSL.inline(8),
          DSL.inline(9),
          DSL.inline(10),
          DSL.inline(11),
          DSL.inline(12),
          DSL.inline(13),
          DSL.inline(14),
          DSL.inline(15),
          DSL.inline(16),
          DSL.inline(17),
          DSL.inline(18),
          DSL.inline(19),
          DSL.inline(20),
          DSL.inline(21)
        ).fetchOne()

        assert(r.asTuple == ((1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21)))
      }
    }
  }

  describe("Record22Ops") {
    describe("asTuple") {
      it("should convert to tuple") {
        val r = dsl.select(
          DSL.inline(1),
          DSL.inline(2),
          DSL.inline(3),
          DSL.inline(4),
          DSL.inline(5),
          DSL.inline(6),
          DSL.inline(7),
          DSL.inline(8),
          DSL.inline(9),
          DSL.inline(10),
          DSL.inline(11),
          DSL.inline(12),
          DSL.inline(13),
          DSL.inline(14),
          DSL.inline(15),
          DSL.inline(16),
          DSL.inline(17),
          DSL.inline(18),
          DSL.inline(19),
          DSL.inline(20),
          DSL.inline(21),
          DSL.inline(22)
        ).fetchOne()

        assert(r.asTuple == ((1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22)))
      }
    }
  }

}
