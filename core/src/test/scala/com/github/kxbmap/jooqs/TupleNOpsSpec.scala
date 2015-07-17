package com.github.kxbmap.jooqs

import org.jooq.impl.DSL
import org.scalatest.FunSpec

class TupleNOpsSpec extends FunSpec {

  import syntax._

  describe("Tuple1Ops") {
    describe("asRow") {
      it("should convert to row") {
        val t = Tuple1(1)
        assert(t.asRow == DSL.row(1))
      }
    }
  }

  describe("Tuple2Ops") {
    describe("asRow") {
      it("should convert to row") {
        val t = (1, 2)
        assert(t.asRow == DSL.row(1, 2))
      }
    }
  }

  describe("Tuple3Ops") {
    describe("asRow") {
      it("should convert to row") {
        val t = (1, 2, 3)
        assert(t.asRow == DSL.row(1, 2, 3))
      }
    }
  }

  describe("Tuple4Ops") {
    describe("asRow") {
      it("should convert to row") {
        val t = (1, 2, 3, 4)
        assert(t.asRow == DSL.row(1, 2, 3, 4))
      }
    }
  }

  describe("Tuple5Ops") {
    describe("asRow") {
      it("should convert to row") {
        val t = (1, 2, 3, 4, 5)
        assert(t.asRow == DSL.row(1, 2, 3, 4, 5))
      }
    }
  }

  describe("Tuple6Ops") {
    describe("asRow") {
      it("should convert to row") {
        val t = (1, 2, 3, 4, 5, 6)
        assert(t.asRow == DSL.row(1, 2, 3, 4, 5, 6))
      }
    }
  }

  describe("Tuple7Ops") {
    describe("asRow") {
      it("should convert to row") {
        val t = (1, 2, 3, 4, 5, 6, 7)
        assert(t.asRow == DSL.row(1, 2, 3, 4, 5, 6, 7))
      }
    }
  }

  describe("Tuple8Ops") {
    describe("asRow") {
      it("should convert to row") {
        val t = (1, 2, 3, 4, 5, 6, 7, 8)
        assert(t.asRow == DSL.row(1, 2, 3, 4, 5, 6, 7, 8))
      }
    }
  }

  describe("Tuple9Ops") {
    describe("asRow") {
      it("should convert to row") {
        val t = (1, 2, 3, 4, 5, 6, 7, 8, 9)
        assert(t.asRow == DSL.row(1, 2, 3, 4, 5, 6, 7, 8, 9))
      }
    }
  }

  describe("Tuple10Ops") {
    describe("asRow") {
      it("should convert to row") {
        val t = (1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        assert(t.asRow == DSL.row(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))
      }
    }
  }

  describe("Tuple11Ops") {
    describe("asRow") {
      it("should convert to row") {
        val t = (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11)
        assert(t.asRow == DSL.row(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11))
      }
    }
  }

  describe("Tuple12Ops") {
    describe("asRow") {
      it("should convert to row") {
        val t = (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)
        assert(t.asRow == DSL.row(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12))
      }
    }
  }

  describe("Tuple13Ops") {
    describe("asRow") {
      it("should convert to row") {
        val t = (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13)
        assert(t.asRow == DSL.row(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13))
      }
    }
  }

  describe("Tuple14Ops") {
    describe("asRow") {
      it("should convert to row") {
        val t = (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14)
        assert(t.asRow == DSL.row(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14))
      }
    }
  }

  describe("Tuple15Ops") {
    describe("asRow") {
      it("should convert to row") {
        val t = (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)
        assert(t.asRow == DSL.row(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15))
      }
    }
  }

  describe("Tuple16Ops") {
    describe("asRow") {
      it("should convert to row") {
        val t = (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16)
        assert(t.asRow == DSL.row(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16))
      }
    }
  }

  describe("Tuple17Ops") {
    describe("asRow") {
      it("should convert to row") {
        val t = (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17)
        assert(t.asRow == DSL.row(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17))
      }
    }
  }

  describe("Tuple18Ops") {
    describe("asRow") {
      it("should convert to row") {
        val t = (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18)
        assert(t.asRow == DSL.row(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18))
      }
    }
  }

  describe("Tuple19Ops") {
    describe("asRow") {
      it("should convert to row") {
        val t = (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19)
        assert(t.asRow == DSL.row(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19))
      }
    }
  }

  describe("Tuple20Ops") {
    describe("asRow") {
      it("should convert to row") {
        val t = (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20)
        assert(t.asRow == DSL.row(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20))
      }
    }
  }

  describe("Tuple21Ops") {
    describe("asRow") {
      it("should convert to row") {
        val t = (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21)
        assert(t.asRow == DSL.row(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21))
      }
    }
  }

  describe("Tuple22Ops") {
    describe("asRow") {
      it("should convert to row") {
        val t = (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22)
        assert(t.asRow == DSL.row(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22))
      }
    }
  }

}
