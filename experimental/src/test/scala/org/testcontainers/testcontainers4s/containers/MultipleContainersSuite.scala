package org.testcontainers.testcontainers4s.containers

import org.scalatest.FreeSpec
import org.testcontainers.testcontainers4s.containers.MySQLContainer.MySQLContainerRuntime
import org.testcontainers.testcontainers4s.containers.PostgreSQLContainer.PostgreSQLContainerRuntime
import org.testcontainers.testcontainers4s.containers.scalatest.TestContainersForAll

class MultipleContainersSuite extends FreeSpec with TestContainersForAll {

  override type Containers = PostgreSQLContainerRuntime and MySQLContainerRuntime

  override def startContainers(): Containers = {
    val pg = PostgreSQLContainer().start()
    val mySql = MySQLContainer().start()

    pg and mySql
  }

  "foo" - {
    "bar" in withContainers { case pg and mySql =>
      assert(pg.jdbcUrl.nonEmpty && mySql.jdbcUrl.nonEmpty)
    }
  }
}
