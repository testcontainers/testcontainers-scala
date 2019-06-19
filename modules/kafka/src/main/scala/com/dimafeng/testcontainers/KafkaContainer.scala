package com.dimafeng.testcontainers

import org.testcontainers.containers.{GenericContainer => OTCGenericContainer, KafkaContainer => OTCKafkaContainer}

class KafkaContainer(confluentPlatformVersion: Option[String] = None,
                     externalZookeeper: Option[String] = None) extends SingleContainer[OTCGenericContainer[_]] {

  type OTCContainer = OTCGenericContainer[T] forSome {type T <: OTCKafkaContainer}

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

}

object KafkaContainer {
  def apply(confluentPlatformVersion: String = null,
            externalZookeeper: String = null): KafkaContainer = {
    new KafkaContainer(Option(confluentPlatformVersion), Option(externalZookeeper))
  }
}
