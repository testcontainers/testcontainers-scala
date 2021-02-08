package com.dimafeng.testcontainers.integration

import java.net.URL

import com.dimafeng.testcontainers.{ForAllTestContainer, GenericContainer}
import org.scalatest.flatspec.AnyFlatSpec
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.images.builder.ImageFromDockerfile

import scala.io.Source

class GenericContainerSpec extends AnyFlatSpec with ForAllTestContainer {
  override val container: GenericContainer = GenericContainer("nginx:latest",
    exposedPorts = Seq(80),
    waitStrategy = Wait.forHttp("/")
  )

  "GenericContainer" should "start nginx and expose 80 port" in {
    assert(Source.fromInputStream(
      new URL(s"http://${container.containerIpAddress}:${container.mappedPort(80)}/").openConnection().getInputStream
    ).mkString.contains("If you see this page, the nginx web server is successfully installed"))
  }
}

class GenericContainerDockerFileSpec extends GenericContainerSpec {
  private val imageFromDockerfile = new ImageFromDockerfile().withFileFromClasspath("Dockerfile", "generic-container-dockerfile")
  override val container: GenericContainer = GenericContainer(imageFromDockerfile,
    exposedPorts = Seq(80),
    waitStrategy = Wait.forHttp("/")
  )
}
