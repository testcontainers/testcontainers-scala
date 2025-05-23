package com.dimafeng.testcontainers

import org.testcontainers.containers.Network

class DynamoContainer(
    dockerImage: String,
    exposedPort: Int,
    network: Option[Network]
) extends GenericContainer(dockerImage = dockerImage) {
  container.withExposedPorts(exposedPort)
  network.foreach(net => container.withNetwork(net))

  def getEndpointUrl: String =
    s"http://${container.getHost}:${container.getMappedPort(exposedPort)}"
}

object DynamoContainer {

  val defaultImage = "amazon/dynamodb-local"
  val defaultTag = "latest"
  val defaultDockerImageName = s"$defaultImage:$defaultTag"

  case class Def(
      dockerImageName: String = DynamoContainer.defaultDockerImageName,
      port: Int = 8000,
      network: Option[Network] = None
  ) extends ContainerDef {
    override type Container = DynamoContainer

    override def createContainer(): DynamoContainer =
      new DynamoContainer(
        dockerImageName,
        port,
        network
      )
  }
}
