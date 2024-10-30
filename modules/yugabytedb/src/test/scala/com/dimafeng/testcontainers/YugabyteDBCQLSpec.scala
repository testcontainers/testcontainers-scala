package com.dimafeng.testcontainers

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql.ResultSet
import com.dimafeng.testcontainers.scalatest.TestContainersForAll
import org.scalatest.flatspec.AnyFlatSpec

class YugabyteDBCQLSpec extends AnyFlatSpec with TestContainersForAll {
  override type Containers = YugabyteDBYCQLContainer

  val keyspace = "test_keyspace"

  override def startContainers(): YugabyteDBYCQLContainer =
    YugabyteDBYCQLContainer
      .Def()
      .withKeyspaceName(keyspace)
      .withUsername("yugabyte")
      .withPassword("yugabyte")
      .withInitScript("init_yql.sql")
      .start()

  "Yugabytedb container" should "be started" in withContainers { yugabytedb =>
    val result = YugabyteDBCQLSpec
      .performQuery(yugabytedb, "SELECT release_version FROM system.local")

    assert(result.wasApplied())
  }

  "Yugabytedb container" should "execute init script" in withContainers { yugabytedbContainer =>
    val result = YugabyteDBCQLSpec
      .performQuery(yugabytedbContainer, s"SELECT greet FROM $keyspace.dsql")

    assert(
      result.wasApplied() &&
        result.one().getString(0) == "Hello DSQL"
    )
  }
}

object YugabyteDBCQLSpec {
  private def performQuery(ycqlContainer: YugabyteDBYCQLContainer, cql: String): ResultSet = {
    val session = CqlSession.builder
      .withKeyspace(ycqlContainer.keyspace)
      .withAuthCredentials(ycqlContainer.username, ycqlContainer.password)
      .withLocalDatacenter(ycqlContainer.localDc)
      .addContactPoint(ycqlContainer.contactPoint)
      .build
    try session.execute(cql)
    finally if (session != null) session.close()
  }

}
