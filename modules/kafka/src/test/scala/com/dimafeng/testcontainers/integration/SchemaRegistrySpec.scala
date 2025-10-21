package com.dimafeng.testcontainers.integration

import com.dimafeng.testcontainers.{ConfluentKafkaContainer, ForAllTestContainer, GenericContainer, MultipleContainers, SchemaRegistryContainer}
import io.confluent.kafka.schemaregistry.ParsedSchema
import io.confluent.kafka.schemaregistry.avro.AvroSchema
import org.apache.kafka.clients.admin.{AdminClient, AdminClientConfig, NewTopic}
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.testcontainers.containers.Network
import org.testcontainers.utility.DockerImageName
import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient
import org.apache.avro.Schema

import java.util.Properties
import scala.collection.JavaConverters._

class SchemaRegistrySpec extends AnyFlatSpec with ForAllTestContainer with Matchers {

  //this should be the same version that your lib is using under the hood
  val kafkaVersion = "8.1.0"

  //these are the default kafka host name but because that may change
  //we need to ensure that these are the values for container network, kafka and the schema registry
  val brokerId = 1
  val hostName = s"kafka$brokerId"

  val topicName = "test"

  //a way to communicate containers
  val network: Network = Network.newNetwork()

  val kafkaContainer: ConfluentKafkaContainer = ConfluentKafkaContainer.Def(DockerImageName.parse(s"confluentinc/cp-kafka:$kafkaVersion")).createContainer()
  val schemaRegistryContainer: SchemaRegistryContainer = SchemaRegistryContainer.Def(network, hostName, kafkaVersion).createContainer()

  kafkaContainer.container
    .withNetwork(network)
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

  def getSchemaRegistryAddress: String = schemaRegistryContainer.schemaUrl


  "Schema registry container" should "be started" in {

    val adminProperties = new Properties()
    adminProperties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, getKafkaAddress)
    val adminClient = AdminClient.create(adminProperties)
    
    // Create a topic
    val createTopicResult = adminClient.createTopics(List(new NewTopic(topicName, 1, 1.toShort)).asJava)
    createTopicResult.values().get(topicName).get()
    
    val registryUrl = getSchemaRegistryAddress
    val schemaRegistryClient: SchemaRegistryClient = new CachedSchemaRegistryClient(registryUrl, 10);

    // Explicitly create a schema in the registry
    val schema: Schema = Schema.create(Schema.Type.STRING)
    val parsedSchema: ParsedSchema = new AvroSchema(schema)
    val schemaId = schemaRegistryClient.register("my-topic-value", parsedSchema)

    val properties = new Properties()
    properties.put("bootstrap.servers", getKafkaAddress)
    properties.put("group.id", "consumer-tutorial")
    properties.put("key.deserializer", classOf[StringDeserializer])
    properties.put("value.deserializer", classOf[StringDeserializer])

    // List topics
    val kafkaConsumer = new KafkaConsumer[String, String](properties)
    val topics = kafkaConsumer.listTopics()

    // Check the topic we created + the one storing schemas exist
    assert(topics.containsKey(topicName))
    assert(topics.containsKey("_schemas"))

    // Check the schema exists 
    assert(schemaRegistryClient.getSchemaById(schemaId) != null)

  }

}
