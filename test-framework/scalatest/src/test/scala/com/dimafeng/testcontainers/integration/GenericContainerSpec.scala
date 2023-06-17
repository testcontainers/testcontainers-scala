package com.dimafeng.testcontainers.integration

import java.net.URI
import com.dimafeng.testcontainers.{ForAllTestContainer, GenericContainer}
import org.scalatest.flatspec.AnyFlatSpec
import org.testcontainers.containers.wait.strategy.Wait

import scala.io.Source

class GenericContainerSpec extends AnyFlatSpec with ForAllTestContainer {
  override val container: GenericContainer = GenericContainer("nginx:latest",
    exposedPorts = Seq(80),
    waitStrategy = Wait.forHttp("/")
  )

  "GenericContainer" should "start nginx and expose 80 port" in {
    assert(Source.fromInputStream(
      new URI(s"http://${container.containerIpAddress}:${container.mappedPort(80)}/").toURL.openConnection().getInputStream
    ).mkString.contains("If you see this page, the nginx web server is successfully installed"))
  }
}

class GenericContainerDockerFileSpec extends GenericContainerSpec {
  // we can't do this in Scala 3 due to https://github.com/lampepfl/dotty/issues/12586 so this is delegated to a small Java class.
  private val imageFromDockerfile = new JavaStub().imageFromDockerFileWithFileFromClasspath("Dockerfile", "generic-container-dockerfile")
  override val container: GenericContainer = GenericContainer(imageFromDockerfile,
    exposedPorts = Seq(80),
    waitStrategy = Wait.forHttp("/")
  )
}
