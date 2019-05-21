package com.dimafeng.testcontainers.integration

import java.net.URL

import com.dimafeng.testcontainers.{FixedHostPortGenericContainer, ForAllTestContainer}
import org.scalatest.FlatSpec

import scala.io.Source

class FixedHostPortContainerSpec extends FlatSpec with ForAllTestContainer {
  override val container = FixedHostPortGenericContainer("nginx:latest",
    waitStrategy = Wait.forHttp("/"),
    exposedHostPort = 8090,
    exposedContainerPort = 80
  )

  "FixedHostPortGenericContainer" should "start nginx and expose 8090 port on host" in {
    assert(container.mappedPort(80) == 8090)
    assert(Source.fromInputStream(
      new URL(s"http://${container.containerIpAddress}:${container.mappedPort(80)}/").openConnection().getInputStream
    ).mkString.contains("If you see this page, the nginx web server is successfully installed"))
  }
}
