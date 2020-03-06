package com.dimafeng.testcontainers

import com.dimafeng.testcontainers.scalatest.TestContainerForAll
import org.scalatest.FlatSpec

class ClickHouseContainerSpec extends FlatSpec with TestContainerForAll {

  override val containerDef: ClickHouseContainer.Def = ClickHouseContainer.Def()

  "ClickHouseContainer" should "work" in withContainers { clickHouseContainer =>
    assert(clickHouseContainer.containerIpAddress.nonEmpty)
  }
}
