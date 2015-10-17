package jooqs.impl

import org.jooq.ConnectionProvider

class ProvidedConnection(provider: ConnectionProvider) extends DelegateConnection(provider.acquire()) {

  override def close(): Unit = {
    provider.release(underlying)
  }
}
