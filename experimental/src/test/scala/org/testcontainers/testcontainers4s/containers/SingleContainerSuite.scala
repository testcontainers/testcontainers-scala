package org.testcontainers.testcontainers4s.containers

import org.scalatest.FreeSpec
import org.testcontainers.testcontainers4s.containers.scalatest.TestContainersForEach

class SingleContainerSuite extends FreeSpec with TestContainersForEach {

  override type ContainerDefs = MySQLContainer.Def

  override def startContainers(): MySQLContainer = {
    MySQLContainer.Def().start()
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

