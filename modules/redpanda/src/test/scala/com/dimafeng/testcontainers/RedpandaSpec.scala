package com.dimafeng.testcontainers

import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.scalatest.flatspec.AnyFlatSpec

import java.util.Properties

class RedpandaSpec extends AnyFlatSpec with ForAllTestContainer {

  override val container: RedpandaContainer = RedpandaContainer()

  "Redpanda container" should "be started" in {
    assert(container.container.isRunning)

    val properties = new Properties()
    properties.put("bootstrap.servers", container.bootstrapServers)
    properties.put("group.id", "consumer-tutorial")
    properties.put("key.deserializer", classOf[StringDeserializer])
    properties.put("value.deserializer", classOf[StringDeserializer])

    val kafkaConsumer = new KafkaConsumer[String, String](properties)
    val topics = kafkaConsumer.listTopics()

    assert(topics.size() >= 0)
  }

}
