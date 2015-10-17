package jooqs.impl

import org.jooq.ConnectionProvider
import org.mockito.Mockito._
import org.scalatest.FunSpec
import org.scalatest.mock.MockitoSugar

class ProvidedConnectionSpec extends FunSpec with MockitoSugar {

  object DummyConnection extends DelegateConnection(null)

  describe("ProvidedConnection") {

    describe("constructor") {
      it("should acquire connection from provider") {
        val provider = mock[ConnectionProvider]

        when(provider.acquire()).thenReturn(DummyConnection)

        val conn = new ProvidedConnection(provider)

        verify(provider).acquire()
        assert(conn.underlying == DummyConnection)
      }
    }

    describe("close") {
      it("should release underlying connection") {
        val provider = mock[ConnectionProvider]

        when(provider.acquire()).thenReturn(DummyConnection)

        val conn = new ProvidedConnection(provider)
        conn.close()

        verify(provider).release(DummyConnection)
      }
    }
  }
}
