package com.dimafeng.testcontainers

import org.testcontainers.containers.{KafkaContainer => JavaKafkaContainer}
import org.testcontainers.utility.DockerImageName

case class KafkaContainer(dockerImageName: DockerImageName = DockerImageName.parse(KafkaContainer.defaultDockerImageName)
                    ) extends SingleContainer[JavaKafkaContainer] {

  override val container: JavaKafkaContainer = new JavaKafkaContainer(dockerImageName)

  def bootstrapServers: String = container.getBootstrapServers
}

object KafkaContainer {

  val defaultImage = "confluentinc/cp-kafka"
  val defaultTag = "7.6.1"
  val defaultDockerImageName = s"$defaultImage:$defaultTag"

  case class Def(dockerImageName: DockerImageName = DockerImageName.parse(KafkaContainer.defaultDockerImageName)
                ) extends ContainerDef {

    override type Container = KafkaContainer

    override def createContainer(): KafkaContainer = {
      new KafkaContainer(dockerImageName)
    }
  }
}
