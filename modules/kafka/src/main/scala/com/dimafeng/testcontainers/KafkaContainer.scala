package com.dimafeng.testcontainers

import org.testcontainers.kafka.{KafkaContainer => JavaKafkaContainer}
import org.testcontainers.utility.DockerImageName

case class KafkaContainer(dockerImageName: DockerImageName = DockerImageName.parse(KafkaContainer.defaultDockerImageName)
                    ) extends SingleContainer[JavaKafkaContainer] {

  override val container: JavaKafkaContainer = new JavaKafkaContainer(dockerImageName)

  def bootstrapServers: String = container.getBootstrapServers
}

object KafkaContainer {

  val defaultDockerImageName = "apache/kafka"

  case class Def(dockerImageName: DockerImageName = DockerImageName.parse(defaultDockerImageName)
                ) extends ContainerDef {

    override type Container = KafkaContainer

    override def createContainer(): KafkaContainer = {
      new KafkaContainer(dockerImageName)
    }
  }
}
