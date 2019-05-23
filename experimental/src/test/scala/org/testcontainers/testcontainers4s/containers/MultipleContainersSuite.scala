package org.testcontainers.testcontainers4s.containers

import org.scalatest.FreeSpec
import org.testcontainers.testcontainers4s.containers.scalatest.TestContainersForAll

class MultipleContainersSuite extends FreeSpec with TestContainersForAll {

  override type ContainerDefs = PostgreSQLContainer.Def andDef MySQLContainer.Def

  override def startContainers(): PostgreSQLContainer and MySQLContainer = {
    val pg = PostgreSQLContainer.Def().start()
    val mySql = MySQLContainer.Def().start()

    pg and mySql
  }

  "foo" - {
    "bar" in withContainers { case pg and mySql =>
      assert(pg.jdbcUrl.nonEmpty && mySql.jdbcUrl.nonEmpty)
    }
  }
}
