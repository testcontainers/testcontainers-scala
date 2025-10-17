package com.dimafeng.testcontainers

import org.scalatest.flatspec.AnyFlatSpec

class ToxiproxySpec extends AnyFlatSpec with ForAllTestContainer  {

  override val container: ToxiproxyContainer = ToxiproxyContainer()

  "Toxiproxy container" should "be started" in {
    assert(container.container.isRunning)
    assert(container.container.getExposedPorts.size > 0)
  }

}
