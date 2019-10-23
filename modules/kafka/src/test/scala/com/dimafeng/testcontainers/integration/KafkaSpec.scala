package com.dimafeng.testcontainers.integration

import java.util.Properties

import com.dimafeng.testcontainers.{ForAllTestContainer, KafkaContainer}
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.scalatest.{FlatSpec, Matchers}

class KafkaSpec extends FlatSpec with ForAllTestContainer with Matchers {

  override val container = KafkaContainer()

  "Kafka container" should "be started" in {

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
