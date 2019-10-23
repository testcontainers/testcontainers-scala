package com.dimafeng.testcontainers.integration

import java.net.InetSocketAddress

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.internal.core.metadata.DefaultEndPoint
import com.dimafeng.testcontainers.{CassandraContainer, ForAllTestContainer}
import org.scalatest.{FlatSpec, Matchers}

class CassandraSpec extends FlatSpec with ForAllTestContainer with Matchers {

  override val container = CassandraContainer()

  "Cassandra container" should "be started" in {

    val session = CqlSession.builder
      .addContactEndPoint(new DefaultEndPoint(InetSocketAddress
      .createUnresolved(container.cassandraContainer.getContainerIpAddress,
                        container.cassandraContainer.getFirstMappedPort.intValue())))
      .withLocalDatacenter("datacenter1").build()
    val rs = session.execute("select release_version from system.local")
    val row = rs.one

    row.getString("release_version").length should be > 0
  }

}
