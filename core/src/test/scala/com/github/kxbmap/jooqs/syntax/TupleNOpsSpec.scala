package com.github.kxbmap.jooqs.syntax

import org.jooq.impl.DSL
import org.scalatest.FunSpec

class TupleNOpsSpec extends FunSpec {

  describe("Tuple1Ops") {
    describe("row") {
      it("should convert to row") {
        val t = Tuple1(1)
        assert(t.row == DSL.row(1))
      }
    }
  }

  describe("Tuple2Ops") {
    describe("row") {
      it("should convert to row") {
        val t = (1, 2)
        assert(t.row == DSL.row(1, 2))
      }
    }
  }

  describe("Tuple3Ops") {
    describe("row") {
      it("should convert to row") {
        val t = (1, 2, 3)
        assert(t.row == DSL.row(1, 2, 3))
      }
    }
  }

  describe("Tuple4Ops") {
    describe("row") {
      it("should convert to row") {
        val t = (1, 2, 3, 4)
        assert(t.row == DSL.row(1, 2, 3, 4))
      }
    }
  }

  describe("Tuple5Ops") {
    describe("row") {
      it("should convert to row") {
        val t = (1, 2, 3, 4, 5)
        assert(t.row == DSL.row(1, 2, 3, 4, 5))
      }
    }
  }

  describe("Tuple6Ops") {
    describe("row") {
      it("should convert to row") {
        val t = (1, 2, 3, 4, 5, 6)
        assert(t.row == DSL.row(1, 2, 3, 4, 5, 6))
      }
    }
  }

  describe("Tuple7Ops") {
    describe("row") {
      it("should convert to row") {
        val t = (1, 2, 3, 4, 5, 6, 7)
        assert(t.row == DSL.row(1, 2, 3, 4, 5, 6, 7))
      }
    }
  }

  describe("Tuple8Ops") {
    describe("row") {
      it("should convert to row") {
        val t = (1, 2, 3, 4, 5, 6, 7, 8)
        assert(t.row == DSL.row(1, 2, 3, 4, 5, 6, 7, 8))
      }
    }
  }

  describe("Tuple9Ops") {
    describe("row") {
      it("should convert to row") {
        val t = (1, 2, 3, 4, 5, 6, 7, 8, 9)
        assert(t.row == DSL.row(1, 2, 3, 4, 5, 6, 7, 8, 9))
      }
    }
  }

  describe("Tuple10Ops") {
    describe("row") {
      it("should convert to row") {
        val t = (1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        assert(t.row == DSL.row(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))
      }
    }
  }

  describe("Tuple11Ops") {
    describe("row") {
      it("should convert to row") {
        val t = (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11)
        assert(t.row == DSL.row(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11))
      }
    }
  }

  describe("Tuple12Ops") {
    describe("row") {
      it("should convert to row") {
        val t = (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)
        assert(t.row == DSL.row(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12))
      }
    }
  }

  describe("Tuple13Ops") {
    describe("row") {
      it("should convert to row") {
        val t = (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13)
        assert(t.row == DSL.row(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13))
      }
    }
  }

  describe("Tuple14Ops") {
    describe("row") {
      it("should convert to row") {
        val t = (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14)
        assert(t.row == DSL.row(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14))
      }
    }
  }

  describe("Tuple15Ops") {
    describe("row") {
      it("should convert to row") {
        val t = (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)
        assert(t.row == DSL.row(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15))
      }
    }
  }

  describe("Tuple16Ops") {
    describe("row") {
      it("should convert to row") {
        val t = (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16)
        assert(t.row == DSL.row(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16))
      }
    }
  }

  describe("Tuple17Ops") {
    describe("row") {
      it("should convert to row") {
        val t = (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17)
        assert(t.row == DSL.row(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17))
      }
    }
  }

  describe("Tuple18Ops") {
    describe("row") {
      it("should convert to row") {
        val t = (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18)
        assert(t.row == DSL.row(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18))
      }
    }
  }

  describe("Tuple19Ops") {
    describe("row") {
      it("should convert to row") {
        val t = (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19)
        assert(t.row == DSL.row(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19))
      }
    }
  }

  describe("Tuple20Ops") {
    describe("row") {
      it("should convert to row") {
        val t = (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20)
        assert(t.row == DSL.row(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20))
      }
    }
  }

  describe("Tuple21Ops") {
    describe("row") {
      it("should convert to row") {
        val t = (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21)
        assert(t.row == DSL.row(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21))
      }
    }
  }

  describe("Tuple22Ops") {
    describe("row") {
      it("should convert to row") {
        val t = (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22)
        assert(t.row == DSL.row(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22))
      }
    }
  }

}
