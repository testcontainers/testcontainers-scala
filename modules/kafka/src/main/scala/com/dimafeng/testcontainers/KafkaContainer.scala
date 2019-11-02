package com.dimafeng.testcontainers

import org.testcontainers.containers.{KafkaContainer => OTCKafkaContainer}

class KafkaContainer(confluentPlatformVersion: Option[String] = None,
                     externalZookeeper: Option[String] = None) extends SingleContainer[OTCKafkaContainer] {

  @deprecated("Please use reflective methods of the scala container or `configure` method")
  val kafkaContainer: OTCKafkaContainer = {
    if (confluentPlatformVersion.isEmpty) {
      new OTCKafkaContainer()
    } else {
      new OTCKafkaContainer(confluentPlatformVersion.get)
    }
  }

  if (externalZookeeper.isEmpty) {
    kafkaContainer.withEmbeddedZookeeper()
  } else {
    kafkaContainer.withExternalZookeeper(externalZookeeper.get)
  }

  override val container: OTCKafkaContainer = kafkaContainer

  def bootstrapServers: String = container.getBootstrapServers
}

object KafkaContainer {
  def apply(confluentPlatformVersion: String = null,
            externalZookeeper: String = null): KafkaContainer = {
    new KafkaContainer(Option(confluentPlatformVersion), Option(externalZookeeper))
  }
}
