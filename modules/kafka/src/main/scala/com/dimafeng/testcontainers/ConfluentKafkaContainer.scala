package com.dimafeng.testcontainers

import org.testcontainers.kafka.{ConfluentKafkaContainer => JavaConfluentKafkaContainer}
import org.testcontainers.utility.DockerImageName

case class ConfluentKafkaContainer(dockerImageName: DockerImageName = DockerImageName.parse(ConfluentKafkaContainer.defaultDockerImageName)
                    ) extends SingleContainer[JavaConfluentKafkaContainer] {

  override val container: JavaConfluentKafkaContainer = new JavaConfluentKafkaContainer(dockerImageName)

  def bootstrapServers: String = container.getBootstrapServers
}

object ConfluentKafkaContainer {

  val defaultImage = "confluentinc/cp-kafka"
  val defaultTag = "7.6.1"
  val defaultDockerImageName = s"$defaultImage:$defaultTag"

  case class Def(dockerImageName: DockerImageName = DockerImageName.parse(defaultDockerImageName)
                ) extends ContainerDef {

    override type Container = ConfluentKafkaContainer

    override def createContainer(): ConfluentKafkaContainer = {
      new ConfluentKafkaContainer(dockerImageName)
    }
  }
}
