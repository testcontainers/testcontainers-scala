package com.dimafeng.testcontainers

import org.scalatest.FlatSpec

class ToxiproxySpec extends FlatSpec with ForAllTestContainer  {

  override val container = ToxiproxyContainer()

  "Toxiproxy container" should "be started" in {
    assert(container.container.isRunning)
    assert(container.container.getExposedPorts.size > 0)
    val proxy = container.proxy("127.0.0.1", 0)
    assert(proxy.getProxyPort > 0)
  }

}
