package com.dimafeng.testcontainers

import org.testcontainers.containers.{ToxiproxyContainer => JavaToxiproxyContainer}

case class ToxiproxyContainer(
  dockerImageName: String = ToxiproxyContainer.defaultDockerImageName
) extends SingleContainer[JavaToxiproxyContainer] {

  import ToxiproxyContainer._

  override val container: JavaToxiproxyContainer = new JavaToxiproxyContainer(dockerImageName)

  def proxy(hostname: String, port: Int): ContainerProxy = container.getProxy(hostname, port)

  def proxy(container: SingleContainer[_], port: Int): ContainerProxy = proxy(container.networkAliases.head, port)
}

object ToxiproxyContainer {

  val defaultDockerImageName = "shopify/toxiproxy:2.1.0"

  type ContainerProxy = JavaToxiproxyContainer.ContainerProxy

  case class Def(
    dockerImageName: String = ToxiproxyContainer.defaultDockerImageName
  ) extends ContainerDef {

    override type Container = ToxiproxyContainer

    override def createContainer(): ToxiproxyContainer = {
      new ToxiproxyContainer(
        dockerImageName
      )
    }
  }

}
