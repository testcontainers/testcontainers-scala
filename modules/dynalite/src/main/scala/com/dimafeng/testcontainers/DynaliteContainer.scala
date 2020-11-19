package com.dimafeng.testcontainers

import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import org.testcontainers.dynamodb.{DynaliteContainer => JavaDynaliteContainer}
import org.testcontainers.utility.DockerImageName

case class DynaliteContainer(
  dockerImageName: DockerImageName = DockerImageName.parse(DynaliteContainer.defaultDockerImageName)
) extends SingleContainer[JavaDynaliteContainer] {

  override val container: JavaDynaliteContainer = {
    val c = new JavaDynaliteContainer(dockerImageName)
    c.withExposedPorts(4567)
    c
  }

  def client: AmazonDynamoDB = container.getClient

  def endpointConfiguration: AwsClientBuilder.EndpointConfiguration = container.getEndpointConfiguration

  def credentials: AWSCredentialsProvider = container.getCredentials
}

object DynaliteContainer {

  val defaultDockerImageName = "quay.io/testcontainers/dynalite:v1.2.1-1"

  case class Def(
    dockerImageName: DockerImageName = DockerImageName.parse(DynaliteContainer.defaultDockerImageName)
  ) extends ContainerDef {

    override type Container = DynaliteContainer

    override def createContainer(): DynaliteContainer = {
      new DynaliteContainer(
        dockerImageName
      )
    }
  }
}
