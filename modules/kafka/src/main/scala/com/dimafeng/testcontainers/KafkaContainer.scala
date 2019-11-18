package com.dimafeng.testcontainers

import org.testcontainers.containers.{KafkaContainer => JavaKafkaContainer}

class KafkaContainer(confluentPlatformVersion: Option[String] = None,
                     externalZookeeper: Option[String] = None) extends SingleContainer[JavaKafkaContainer] {

  @deprecated("Please use reflective methods of the scala container or `configure` method")
  val kafkaContainer: JavaKafkaContainer = {
    if (confluentPlatformVersion.isEmpty) {
      new JavaKafkaContainer()
    } else {
      new JavaKafkaContainer(confluentPlatformVersion.get)
    }
  }

  if (externalZookeeper.isEmpty) {
    kafkaContainer.withEmbeddedZookeeper()
  } else {
    kafkaContainer.withExternalZookeeper(externalZookeeper.get)
  }

  override val container: JavaKafkaContainer = kafkaContainer

  def bootstrapServers: String = container.getBootstrapServers
}

object KafkaContainer {

  val defaultTag = "5.2.1"

  def apply(confluentPlatformVersion: String = null,
            externalZookeeper: String = null): KafkaContainer = {
    new KafkaContainer(Option(confluentPlatformVersion), Option(externalZookeeper))
  }

  case class Def(
    confluentPlatformVersion: String = defaultTag,
    externalZookeeper: Option[String] = None
  ) extends ContainerDef {

    override type Container = KafkaContainer

    override def createContainer(): KafkaContainer = {
      new KafkaContainer(
        confluentPlatformVersion = Some(confluentPlatformVersion),
        externalZookeeper = externalZookeeper
      )
    }
  }
}
