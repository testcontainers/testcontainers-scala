package com.dimafeng.testcontainers.integration

import java.util.Properties
import com.dimafeng.testcontainers.{ForAllTestContainer, ConfluentKafkaContainer}
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ConfluentKafkaSpec extends AnyFlatSpec with ForAllTestContainer with Matchers {

  override val container: ConfluentKafkaContainer = ConfluentKafkaContainer()

  "Confluent Kafka container" should "be started" in {

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
