package com.dimafeng.testcontainers

import com.dimafeng.testcontainers.scalatest.TestContainerForAll
import org.scalatest.flatspec.AnyFlatSpec

class ClickHouseContainerSpec extends AnyFlatSpec with TestContainerForAll {

  override val containerDef: ClickHouseContainer.Def = ClickHouseContainer.Def()

  "ClickHouseContainer" should "work" in withContainers { clickHouseContainer =>
    assert(clickHouseContainer.containerIpAddress.nonEmpty)
  }
}
