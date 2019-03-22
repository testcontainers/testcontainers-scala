package org.testcontainers.testcontainers4s.containers

import org.scalatest.FreeSpec
import org.testcontainers.testcontainers4s.containers.scalatest.ForAllTestContainer

class SingleContainerSuite extends FreeSpec with ForAllTestContainer[MySQLContainer.Def] {

  override def startContainers() = {
    new MySQLContainer.Def().start
  }

  "foo" - {
    "bar" in withContainers { db =>
      assert(db.jdbcUrl.nonEmpty)
    }
  }
}

