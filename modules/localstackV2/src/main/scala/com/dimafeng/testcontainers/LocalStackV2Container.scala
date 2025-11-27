package com.dimafeng.testcontainers

import java.net.URI
import org.testcontainers.localstack.{LocalStackContainer => JavaLocalStackContainer}
import org.testcontainers.utility.DockerImageName
import software.amazon.awssdk.auth.credentials.{AwsBasicCredentials, StaticCredentialsProvider}
import software.amazon.awssdk.regions.Region

case class LocalStackV2Container(
    tag: String = LocalStackV2Container.defaultTag,
    services: Seq[String] = Seq.empty
) extends SingleContainer[JavaLocalStackContainer] {

  override val container: JavaLocalStackContainer = {
    val c = new JavaLocalStackContainer(DockerImageName.parse(LocalStackV2Container.defaultImage + ":" + tag))
    c.withServices(services: _*)
    c
  }

  def endpoint: URI =
    container.getEndpoint

  def staticCredentialsProvider: StaticCredentialsProvider =
    StaticCredentialsProvider.create(
      AwsBasicCredentials.create(container.getAccessKey, container.getSecretKey)
    )

  def region: Region = Region.of(container.getRegion)
}

object LocalStackV2Container {
  val defaultImage: String = "localstack/localstack"
  val defaultTag: String = "4.0.3"

  case class Def(
      tag: String = LocalStackV2Container.defaultTag,
      services: Seq[String] = Seq.empty
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
