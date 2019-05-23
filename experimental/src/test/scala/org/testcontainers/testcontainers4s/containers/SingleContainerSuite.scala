package org.testcontainers.testcontainers4s.containers

import org.scalatest.FreeSpec
import org.testcontainers.testcontainers4s.containers.scalatest.TestContainerForEach

class SingleContainerSuite extends FreeSpec with TestContainerForEach {

  override type Container = MySQLContainer
  override val containerDef = MySQLContainer.Def()

  "foo" - {
    "bar" in withContainers { db =>
      assert(db.jdbcUrl.nonEmpty)
    }
    "baz" in withContainers { db =>
      assert(db.jdbcUrl.nonEmpty)
    }
  }
}

