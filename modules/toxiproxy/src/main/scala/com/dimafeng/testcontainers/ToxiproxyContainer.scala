package com.dimafeng.testcontainers

import org.testcontainers.toxiproxy.{ToxiproxyContainer => JavaToxiproxyContainer}
import org.testcontainers.utility.DockerImageName

case class ToxiproxyContainer(
  dockerImageName: DockerImageName = DockerImageName.parse(ToxiproxyContainer.defaultDockerImageName)
) extends SingleContainer[JavaToxiproxyContainer] {

  override val container: JavaToxiproxyContainer = new JavaToxiproxyContainer(dockerImageName)

}

object ToxiproxyContainer {

  val defaultDockerImageName = "shopify/toxiproxy:2.1.4"

  case class Def(
    dockerImageName: DockerImageName = DockerImageName.parse(ToxiproxyContainer.defaultDockerImageName)
  ) extends ContainerDef {

    override type Container = ToxiproxyContainer

    override def createContainer(): ToxiproxyContainer = {
      new ToxiproxyContainer(
        dockerImageName
      )
    }
  }

}
