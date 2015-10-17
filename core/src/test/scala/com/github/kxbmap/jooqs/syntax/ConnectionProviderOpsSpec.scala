package com.github.kxbmap.jooqs.syntax

import org.jooq.ConnectionProvider
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.FunSpec
import org.scalatest.mock.MockitoSugar

class ConnectionProviderOpsSpec extends FunSpec with MockitoSugar {

  describe("ConnectionProviderOps") {

    describe("withConnection") {

      it("should calls acquire/release") {
        val provider = mock[ConnectionProvider]

        val v = provider.withConnection(c => 42)
        assert(v == 42)

        verify(provider).acquire()
        verify(provider).release(any())
      }
    }

  }

}
