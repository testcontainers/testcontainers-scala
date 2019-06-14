package org.testcontainers.testcontainers4s.containers

import org.scalatest.{FlatSpec, FreeSpec}
import org.testcontainers.testcontainers4s.containers.scalatest.TestContainerForEach

class SingleContainerSuite extends FreeSpec with TestContainerForEach {

  override val container = MySQLContainer()

  "foo" - {
    "bar" in withContainers { db =>
      assert(db.jdbcUrl.nonEmpty)
    }
    "baz" in withContainers { db =>
      assert(db.jdbcUrl.nonEmpty)
    }
  }
}

class OldContainerSpec extends FlatSpec with TestContainerForEach {

  override val container = MySQLContainer()

  it should "test" in {
    assert(container.jdbcUrl.nonEmpty)
  }
}
