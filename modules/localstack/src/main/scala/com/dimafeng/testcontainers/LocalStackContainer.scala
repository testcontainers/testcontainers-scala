package com.dimafeng.testcontainers

import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.client.builder.AwsClientBuilder
import org.testcontainers.containers.localstack.{LocalStackContainer => JavaLocalStackContainer}

case class LocalStackContainer(
  tag: String = LocalStackContainer.defaultTag,
  services: Seq[LocalStackContainer.Service] = Seq.empty
) extends SingleContainer[JavaLocalStackContainer] {

  override val container: JavaLocalStackContainer = {
    val c = new JavaLocalStackContainer(tag)
    c.withServices(services: _*)
    c
  }

  def endpointConfiguration(service: LocalStackContainer.Service): AwsClientBuilder.EndpointConfiguration =
    container.getEndpointConfiguration(service)

  def defaultCredentialsProvider: AWSCredentialsProvider = container.getDefaultCredentialsProvider
}

object LocalStackContainer {

  val defaultTag = "0.12.12"

  type Service = JavaLocalStackContainer.Service

  case class Def(
    tag: String = LocalStackContainer.defaultTag,
    services: Seq[LocalStackContainer.Service] = Seq.empty
  ) extends ContainerDef {

    override type Container = LocalStackContainer

    override def createContainer(): LocalStackContainer = {
      new LocalStackContainer(
        tag,
        services
      )
    }
  }
}
