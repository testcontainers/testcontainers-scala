package com.dimafeng.testcontainers.integration

import java.io.File
import java.time.Duration

import com.dimafeng.testcontainers.{DockerComposeContainer, WaitingForService}
import org.scalatest.FlatSpec
import org.testcontainers.containers.wait.strategy.Wait

class ComposeWaitingForSpec extends FlatSpec {
  "DockerComposeContainer" should "wait for service" in {
    val container = DockerComposeContainer(
      Seq(new File(getClass.getClassLoader.getResource("docker-compose.yml").getPath)),
      waitingFor = Some(WaitingForService("redis", Wait.forLogMessage(".*Ready to accept connections\\n", 1)))
    )
    container.start() // blocks until successful or timeout

    assert(container.getContainerByServiceName("redis_1").get.isRunning)
  }

  "DockerComposeContainer" should "throw exception when timeout occurs" in {
    val waitStrategy = Wait.forLogMessage("this is never happen", 1)
      .withStartupTimeout(Duration.ofSeconds(5L))

    val container = DockerComposeContainer(
      Seq(new File(getClass.getClassLoader.getResource("docker-compose.yml").getPath)),
      waitingFor = Some(WaitingForService("redis", waitStrategy))
    )
    val caught = intercept[RuntimeException] {
      container.start()
    }
    assert(caught.getMessage.contains("Timed out waiting for log output matching"))
  }
}