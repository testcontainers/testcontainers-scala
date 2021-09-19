package com.dimafeng.testcontainers

import org.testcontainers.containers.Network

import scala.collection.JavaConverters._
import org.testcontainers.containers.{GenericContainer => JavaGenericContainer}

class SchemaRegistryContainer(network: Network, kafkaHost: String, confluentPlatformVersion: String, schemaPort: Int)
    extends GenericContainer(
      new JavaGenericContainer(s"confluentinc/cp-schema-registry:$confluentPlatformVersion")
    ) {

  container.withExposedPorts(schemaPort)
  container.withNetwork(network)
  container.setEnv(
    List(
      s"SCHEMA_REGISTRY_HOST_NAME=${container.getHost}",
      s"SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS=$kafkaHost:9092"
    ).asJava
  )

  def schemaUrl: String =
    s"http://${container.getHost}:${container.getMappedPort(schemaPort)}"

}

object SchemaRegistryContainer {

  val defaultSchemaPort = 8081

  def apply(
    network: Network,
    kafkaHost: String,
    confluentPlatformVersion: String = KafkaContainer.defaultTag,
    schemaPort: Int = defaultSchemaPort
  ): SchemaRegistryContainer =
    new SchemaRegistryContainer(network, kafkaHost, confluentPlatformVersion, schemaPort)

  case class Def(
    network: Network,
    kafkaHost: String,
    confluentPlatformVersion: String = KafkaContainer.defaultTag,
    schemaPort: Int = defaultSchemaPort
  ) extends ContainerDef {

    override type Container = SchemaRegistryContainer

    override def createContainer(): SchemaRegistryContainer =
      new SchemaRegistryContainer(network, kafkaHost, confluentPlatformVersion, schemaPort)
  }

}
