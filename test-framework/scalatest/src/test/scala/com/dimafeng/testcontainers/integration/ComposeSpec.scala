package com.dimafeng.testcontainers.integration

import java.io.File

import com.dimafeng.testcontainers.{DockerComposeContainer, ExposedService, ForAllTestContainer}
import org.scalatest.flatspec.AnyFlatSpec

class ComposeSpec extends AnyFlatSpec with ForAllTestContainer {
  override val container: DockerComposeContainer = DockerComposeContainer(
    new File(getClass.getClassLoader.getResource("docker-compose.yml").getPath),
    Seq(ExposedService("redis", 6379))
  )

  "DockerComposeContainer" should "retrieve non-0 port for any of services" in {
    assert(container.getServicePort("redis", 6379) > 0)
  }
}

class ComposeSpecWithImplicitConversions extends ComposeSpec {
  override val container: DockerComposeContainer = DockerComposeContainer(
    Seq(new File(getClass.getClassLoader.getResource("docker-compose.yml").getPath)),
    exposedServices = Seq(ExposedService("redis", 6379))
  )
}