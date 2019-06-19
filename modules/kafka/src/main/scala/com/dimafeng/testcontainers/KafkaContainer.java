package com.dimafeng.testcontainers;

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
