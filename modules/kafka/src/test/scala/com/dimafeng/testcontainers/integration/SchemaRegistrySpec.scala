package com.dimafeng.testcontainers.integration

import com.dimafeng.testcontainers.{ForAllTestContainer, GenericContainer, KafkaContainer, MultipleContainers, SchemaRegistryContainer}
import org.apache.kafka.clients.admin.{AdminClient, AdminClientConfig, NewTopic}
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.testcontainers.containers.Network
import org.testcontainers.utility.DockerImageName

import java.util.Properties
import scala.collection.JavaConverters._

class SchemaRegistrySpec extends AnyFlatSpec with ForAllTestContainer with Matchers {

  //this should be the same version that your lib is using under the hood
  val kafkaVersion = "6.1.1"
  val kafkaDockerImage = DockerImageName.parse(s"confluentinc/cp-kafka:$kafkaVersion")

  //these are the default kafka host name but because that may change
  //we need to ensure that these are the values for container network, kafka and the schema registry
  val brokerId = 1
  val hostName = s"kafka$brokerId"

  val topicName = "test"

  //a way to communicate containers
  val network: Network = Network.newNetwork()

  val kafkaContainer: KafkaContainer = KafkaContainer.Def(network, kafkaDockerImage).createContainer()
  val schemaRegistryContainer: GenericContainer = SchemaRegistryContainer.Def(network, hostName, kafkaVersion).createContainer()

  kafkaContainer.container
    .withNetworkAliases(hostName)
    .withEnv(
      Map[String, String](
        "KAFKA_BROKER_ID" -> brokerId.toString,
        "KAFKA_HOST_NAME" -> hostName,
        "KAFKA_AUTO_CREATE_TOPICS_ENABLE" -> "false"
      ).asJava
    )

  override val container: MultipleContainers = MultipleContainers(kafkaContainer, schemaRegistryContainer)

  def getKafkaAddress: String = kafkaContainer.bootstrapServers

  def getSchemaRegistryAddress: String =
    s"http://${schemaRegistryContainer.container.getHost}:${schemaRegistryContainer.container.getMappedPort(SchemaRegistryContainer.defaultSchemaPort)}"


  "Schema registry container" should "be started" in {

    val adminProperties = new Properties()
    adminProperties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, getKafkaAddress)

    val adminClient = AdminClient.create(adminProperties)
    val createTopicResult = adminClient.createTopics(List(new NewTopic(topicName, 1, 1)).asJava)
    createTopicResult.values().get(topicName).get()

    val properties = new Properties()
    properties.put("bootstrap.servers", getKafkaAddress)
    properties.put("group.id", "consumer-tutorial")
    properties.put("key.deserializer", classOf[StringDeserializer])
    properties.put("value.deserializer", classOf[StringDeserializer])

    val kafkaConsumer = new KafkaConsumer[String, String](properties)
    val topics = kafkaConsumer.listTopics()

    assert(topics.containsKey(topicName))
    assert(topics.containsKey("_schemas"))
  }

}
