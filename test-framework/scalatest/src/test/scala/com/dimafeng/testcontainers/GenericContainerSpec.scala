package com.dimafeng.testcontainers

import org.scalatest.flatspec.AnyFlatSpec
import scala.io.Source
import java.net.URL
import com.dimafeng.testcontainers.scalatest.TestContainerForAll
import org.testcontainers.containers.wait.strategy.Wait
import com.dimafeng.testcontainers.GenericContainer.Def

class GenericContainerSpec extends AnyFlatSpec with TestContainerForAll {
  override val containerDef: Def[GenericContainer] = GenericContainer.Def("nginx:latest",
    exposedPorts = Seq(80),
    waitStrategy = Wait.forHttp("/")
  )

  "GenericContainer" should "start nginx and expose 80 port" in withContainers { case container =>
    assert(Source.fromInputStream(
      new URL(s"http://${container.containerIpAddress}:${container.mappedPort(80)}/").openConnection().getInputStream
    ).mkString.contains("If you see this page, the nginx web server is successfully installed"))
  }
}
