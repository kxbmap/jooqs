package jooqs.impl

import java.sql.ResultSet._
import java.sql.{Connection, Statement}
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.FunSpec
import org.scalatest.mock.MockitoSugar

class DelegateConnectionSpec extends FunSpec with MockitoSugar {

  describe("DelegateConnection") {
    describe("method calls") {
      they("should delegate to underlying connection") {
        val underlying = mock[Connection]
        val conn = new DelegateConnection(underlying)

        conn.setAutoCommit(true)
        verify(underlying).setAutoCommit(anyBoolean())

        conn.setHoldability(HOLD_CURSORS_OVER_COMMIT)
        verify(underlying).setHoldability(anyInt())

        conn.clearWarnings()
        verify(underlying).clearWarnings()

        conn.getNetworkTimeout()
        verify(underlying).getNetworkTimeout

        conn.createBlob()
        verify(underlying).createBlob()

        conn.createSQLXML()
        verify(underlying).createSQLXML()

        conn.setSavepoint()
        verify(underlying).setSavepoint()

        conn.setSavepoint("name")
        verify(underlying).setSavepoint(anyString())

        conn.createNClob()
        verify(underlying).createNClob()

        conn.getTransactionIsolation()
        verify(underlying).getTransactionIsolation

        conn.getClientInfo()
        verify(underlying).getClientInfo

        conn.getClientInfo("name")
        verify(underlying).getClientInfo(anyString())

        conn.getSchema()
        verify(underlying).getSchema

        conn.setNetworkTimeout(null, 1000)
        verify(underlying).setNetworkTimeout(any(), anyInt())

        conn.getMetaData()
        verify(underlying).getMetaData

        conn.getTypeMap()
        verify(underlying).getTypeMap

        conn.rollback()
        verify(underlying).rollback()

        conn.rollback(null)
        verify(underlying).rollback(any())

        conn.createStatement()
        verify(underlying).createStatement()

        conn.createStatement(TYPE_FORWARD_ONLY, CONCUR_READ_ONLY)
        verify(underlying).createStatement(anyInt(), anyInt())

        conn.createStatement(TYPE_FORWARD_ONLY, CONCUR_READ_ONLY, HOLD_CURSORS_OVER_COMMIT)
        verify(underlying).createStatement(anyInt(), anyInt(), anyInt())

        conn.getHoldability()
        verify(underlying).getHoldability

        conn.setReadOnly(true)
        verify(underlying).setReadOnly(anyBoolean())

        conn.setClientInfo("name", "value")
        verify(underlying).setClientInfo(anyString(), anyString())

        conn.setClientInfo(null)
        verify(underlying).setClientInfo(any())

        conn.isReadOnly()
        verify(underlying).isReadOnly

        conn.setTypeMap(null)
        verify(underlying).setTypeMap(any())

        conn.getCatalog()
        verify(underlying).getCatalog

        conn.createClob()
        verify(underlying).createClob()

        conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED)
        verify(underlying).setTransactionIsolation(anyInt())

        conn.nativeSQL("sql")
        verify(underlying).nativeSQL(anyString())

        conn.prepareCall("sql")
        verify(underlying).prepareCall(anyString())

        conn.prepareCall("sql", TYPE_FORWARD_ONLY, CONCUR_READ_ONLY)
        verify(underlying).prepareCall(anyString(), anyInt(), anyInt())

        conn.prepareCall("sql", TYPE_FORWARD_ONLY, CONCUR_READ_ONLY, HOLD_CURSORS_OVER_COMMIT)
        verify(underlying).prepareCall(anyString(), anyInt(), anyInt(), anyInt())

        conn.createArrayOf("typeName", Array[AnyRef]())
        verify(underlying).createArrayOf(anyString(), any())

        conn.setCatalog("catalog")
        verify(underlying).setCatalog(anyString())

        conn.close()
        verify(underlying).close()

        conn.getAutoCommit()
        verify(underlying).getAutoCommit

        conn.abort(null)
        verify(underlying).abort(any())

        conn.isValid(1000)
        verify(underlying).isValid(anyInt())

        conn.prepareStatement("sql")
        verify(underlying).prepareStatement(anyString())

        conn.prepareStatement("sql", TYPE_FORWARD_ONLY, CONCUR_READ_ONLY)
        verify(underlying).prepareStatement(anyString(), anyInt(), anyInt())

        conn.prepareStatement("sql", TYPE_FORWARD_ONLY, CONCUR_READ_ONLY, HOLD_CURSORS_OVER_COMMIT)
        verify(underlying).prepareStatement(anyString(), anyInt(), anyInt(), anyInt())

        conn.prepareStatement("sql", Statement.RETURN_GENERATED_KEYS)
        verify(underlying).prepareStatement(anyString(), anyInt())

        conn.prepareStatement("sql", Array[Int]())
        verify(underlying).prepareStatement(anyString(), any[Array[Int]]())

        conn.prepareStatement("sql", Array[String]())
        verify(underlying).prepareStatement(anyString(), any[Array[String]]())

        conn.releaseSavepoint(null)
        verify(underlying).releaseSavepoint(any())

        conn.isClosed()
        verify(underlying).isClosed

        conn.createStruct("typeName", Array[AnyRef]())
        verify(underlying).createStruct(anyString(), any())

        conn.getWarnings()
        verify(underlying).getWarnings

        conn.setSchema("schema")
        verify(underlying).setSchema(anyString())

        conn.commit()
        verify(underlying).commit()

        conn.unwrap(null)
        verify(underlying).unwrap(any())

        conn.isWrapperFor(null)
        verify(underlying).isWrapperFor(any())
      }
    }
  }

}
