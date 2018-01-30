package com.dimafeng.testcontainers.integration

import java.net.URL

import com.dimafeng.testcontainers.{ForAllTestContainer, GenericContainer}
import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner
import org.testcontainers.containers.wait.Wait

import scala.io.Source

class GenericContainerSpec extends FlatSpec with ForAllTestContainer {
  override val container = GenericContainer("nginx:latest",
    exposedPorts = Seq(80),
    waitStrategy = Wait.forHttp("/")
  )

  "GenericContainer" should "start nginx and expose 80 port" in {
    assert(Source.fromInputStream(
      new URL(s"http://${container.containerIpAddress}:${container.mappedPort(80)}/").openConnection().getInputStream
    ).mkString.contains("If you see this page, the nginx web server is successfully installed"))
  }
}
