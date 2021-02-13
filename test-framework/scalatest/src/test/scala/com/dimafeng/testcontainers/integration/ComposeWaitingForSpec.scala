package com.dimafeng.testcontainers.integration

import java.io.File
import java.time.Duration.ofSeconds

import com.dimafeng.testcontainers.{DockerComposeContainer, ForAllTestContainer, WaitingForService}
import org.scalatest.flatspec.AnyFlatSpec
import org.testcontainers.containers.wait.strategy.Wait

class ComposeWaitingForSpec extends AnyFlatSpec with ForAllTestContainer {
  override val container: DockerComposeContainer = DockerComposeContainer(
    Seq(new File(getClass.getClassLoader.getResource("docker-compose.yml").getPath)),
    waitingFor = Some(WaitingForService("redis", Wait.forLogMessage(".*Ready to accept connections\\n", 1)))
  )

  "DockerComposeContainer" should "wait for service" in {
    // container.start() should blocks until successful or timeout
    assert(container.getContainerByServiceName("redis_1").get.isRunning)
  }
}

class ComposeWaitingForWithTimeoutSpec extends AnyFlatSpec {

  "DockerComposeContainer" should "throw exception when timeout occurs" in {
    val waitStrategy = Wait.forLogMessage("this is never happen", 1)
      .withStartupTimeout(ofSeconds(5L))

    val container = DockerComposeContainer(
      Seq(new File(getClass.getClassLoader.getResource("docker-compose.yml").getPath)),
      waitingFor = Some(WaitingForService("redis", waitStrategy))
    )
    val caught = intercept[RuntimeException] {
      container.start()
    }
    container.stop()

    assert(caught.getMessage.contains("Timed out waiting for log output matching"))
  }
}