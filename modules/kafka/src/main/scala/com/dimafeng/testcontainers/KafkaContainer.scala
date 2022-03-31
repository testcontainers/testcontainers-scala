package com.dimafeng.testcontainers

import org.testcontainers.containers.{KafkaContainer => JavaKafkaContainer}
import org.testcontainers.utility.DockerImageName

case class KafkaContainer(dockerImageName: DockerImageName = DockerImageName.parse(KafkaContainer.defaultDockerImageName)
                    ) extends SingleContainer[JavaKafkaContainer] {

  override val container: JavaKafkaContainer = new JavaKafkaContainer(dockerImageName)

  def bootstrapServers: String = container.getBootstrapServers
}

object KafkaContainer {

  val defaultImage = Option(System.getProperty("os.arch")) match {
    /* 
    * The official Confluent docker image does not work with ARM64.
    * See https://github.com/confluentinc/common-docker/issues/117,
    * https://github.com/confluentinc/kafka-images/issues/80
    */
    case Some("aarch64") => "niciqy/cp-kafka-arm64"
    case _ => "confluentinc/cp-kafka"
  }
  val defaultTag = "7.0.1"
  val defaultDockerImageName = s"$defaultImage:$defaultTag"

  case class Def(dockerImageName: DockerImageName = DockerImageName.parse(KafkaContainer.defaultDockerImageName)
                ) extends ContainerDef {

    override type Container = KafkaContainer

    override def createContainer(): KafkaContainer = {
      new KafkaContainer(dockerImageName)
    }
  }
}