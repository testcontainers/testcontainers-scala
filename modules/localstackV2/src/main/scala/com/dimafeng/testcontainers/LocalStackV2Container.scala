package com.dimafeng.testcontainers

import java.net.URI

import org.testcontainers.containers.localstack.{LocalStackContainer => JavaLocalStackContainer}
import software.amazon.awssdk.auth.credentials.{AwsBasicCredentials, StaticCredentialsProvider}
import software.amazon.awssdk.regions.Region

case class LocalStackV2Container(
  tag: String = LocalStackV2Container.defaultTag,
  services: Seq[LocalStackV2Container.Service] = Seq.empty
) extends SingleContainer[JavaLocalStackContainer] {

  override val container: JavaLocalStackContainer = {
    val c = new JavaLocalStackContainer(tag)
    c.withServices(services: _*)
    c
  }

  def endpointOverride(service: LocalStackV2Container.Service): URI =
    container.getEndpointOverride(service)

  def staticCredentialsProvider: StaticCredentialsProvider =
    StaticCredentialsProvider.create(
      AwsBasicCredentials.create(container.getAccessKey, container.getSecretKey)
    )

  def region: Region = Region.of(container.getRegion)
}

object LocalStackV2Container {

  val defaultTag = "0.9.4"

  type Service = JavaLocalStackContainer.Service

  case class Def(
                  tag: String = LocalStackV2Container.defaultTag,
                  services: Seq[LocalStackV2Container.Service] = Seq.empty
  ) extends ContainerDef {

    override type Container = LocalStackV2Container

    override def createContainer(): LocalStackV2Container = {
      new LocalStackV2Container(
        tag,
        services
      )
    }
  }
}
