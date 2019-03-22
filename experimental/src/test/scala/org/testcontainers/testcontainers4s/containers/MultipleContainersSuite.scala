package org.testcontainers.testcontainers4s.containers

import org.scalatest.FreeSpec
import org.testcontainers.testcontainers4s.containers.scalatest.ForAllTestContainer

class MultipleContainersSuite extends FreeSpec with ForAllTestContainer[PostgreSQLContainer.Def andDef MySQLContainer.Def] {

  override def startContainers() = {
    val pg = new PostgreSQLContainer.Def().start
    val mySql = new MySQLContainer.Def().start

    pg and mySql
  }

  "foo" - {
    "bar" in withContainers { case pg and mySql =>
      assert(pg.jdbcUrl.nonEmpty && mySql.jdbcUrl.nonEmpty)
    }
  }
}
