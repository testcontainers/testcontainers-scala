package org.testcontainers.testcontainers4s.containers

import org.scalatest.FreeSpec
import org.testcontainers.testcontainers4s.containers.scalatest.TestContainersForEach

class SingleContainerSuite extends FreeSpec with TestContainersForEach[MySQLContainer.Def] {

  override def startContainers() = {
    new MySQLContainer.Def().start
  }

  "foo" - {
    "bar" in withContainers { db =>
      assert(db.jdbcUrl.nonEmpty)
    }
    "baz" in withContainers { db =>
      assert(db.jdbcUrl.nonEmpty)
    }
  }
}

