package org.testcontainers.testcontainers4s.containers

import org.scalatest.FreeSpec
import org.testcontainers.testcontainers4s.containers.scalatest.ForAllTestContainer

class SingleContainerSuite extends FreeSpec with ForAllTestContainer[PostgreSQLContainer.Def] {

  override def startContainers = {
    new PostgreSQLContainer.Def().start
  }

  "foo" - {
    "bar" in withContainers { pg1 =>
      assert(pg1.jdbcUrl.nonEmpty)
    }
  }
}

