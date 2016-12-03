package jooqs.syntax

import org.jooq.ConnectionProvider
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatest.FunSpec
import org.scalatest.mockito.MockitoSugar

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
