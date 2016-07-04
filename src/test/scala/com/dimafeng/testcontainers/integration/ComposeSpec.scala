package com.dimafeng.testcontainers.integration

import java.io.File

import com.dimafeng.testcontainers.{DockerComposeContainer, ForAllTestContainer}
import org.scalatest.FlatSpec

class ComposeSpec extends FlatSpec with ForAllTestContainer {
  override val container = DockerComposeContainer(new File("src/test/resources/docker-compose.yml"), exposedService = Map("redis_1" -> 6379))

  "DockerComposeContainer" should "retrieve non-0 port for any of services" in {
    assert(container.getServicePort("redis_1", 6379) > 0)
  }
}
