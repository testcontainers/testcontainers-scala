package com.dimafeng.testcontainers

import org.testcontainers.containers.{Network, KafkaContainer => JavaKafkaContainer}
import org.testcontainers.utility.DockerImageName

case class KafkaContainer(
  override val network: Network = KafkaContainer.defaultNetwork,
  dockerImageName: DockerImageName = KafkaContainer.defaultDockerImage
) extends SingleContainer[JavaKafkaContainer] {

  override val container: JavaKafkaContainer = new JavaKafkaContainer(dockerImageName).withNetwork(network)

  def bootstrapServers: String = container.getBootstrapServers
}

object KafkaContainer {

  val defaultTag = "7.2.0"

  private val defaultDockerImage = DockerImageName.parse(s"confluentinc/cp-kafka:$defaultTag")
  private def defaultNetwork: Network = Network.newNetwork()

  case class Def(
    network: Network = defaultNetwork,
    dockerImageName: DockerImageName = defaultDockerImage
  ) extends ContainerDef {

    override type Container = KafkaContainer

    override def createContainer(): KafkaContainer = {
      new KafkaContainer(network, dockerImageName)
    }
  }
}