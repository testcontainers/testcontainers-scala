package com.dimafeng.testcontainers.integration

import java.net.{URI, URL}
import com.dimafeng.testcontainers.{FixedHostPortGenericContainer, ForAllTestContainer}
import org.scalatest.flatspec.AnyFlatSpec
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy

import scala.io.Source

class FixedHostPortContainerSpec extends AnyFlatSpec with ForAllTestContainer {
  override val container: FixedHostPortGenericContainer = FixedHostPortGenericContainer("nginx:latest",
    waitStrategy = new HttpWaitStrategy().forPath("/"),
    exposedHostPort = 8090,
    exposedContainerPort = 80
  )

  "FixedHostPortGenericContainer" should "start nginx and expose 8090 port on host" in {
    assert(container.mappedPort(80) == 8090)
    assert(Source.fromInputStream(
      new URI(s"http://${container.containerIpAddress}:${container.mappedPort(80)}/").toURL.openConnection().getInputStream
    ).mkString.contains("If you see this page, the nginx web server is successfully installed"))
  }
}
