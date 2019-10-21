package com.dimafeng.testcontainers.integration

import java.io.File

import com.dimafeng.testcontainers.{DockerComposeContainer, ExposedService, ForAllTestContainer}
import org.scalatest.FlatSpec

class ComposeSpec extends FlatSpec with ForAllTestContainer {
  override val container = DockerComposeContainer(
    new File(getClass.getClassLoader.getResource("docker-compose.yml").getPath),
    Seq(ExposedService("redis_1", 6379))
  )

  "DockerComposeContainer" should "retrieve non-0 port for any of services" in {
    assert(container.getServicePort("redis_1", 6379) > 0)
  }
}

class ComposeSpecWithImplicitConversions extends ComposeSpec {
  override val container = DockerComposeContainer(
    Seq(new File(getClass.getClassLoader.getResource("docker-compose.yml").getPath)),
    exposedServices = Seq(ExposedService("redis_1", 6379))
  )
}