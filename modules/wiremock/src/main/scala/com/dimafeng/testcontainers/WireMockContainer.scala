package com.dimafeng.testcontainers

import org.wiremock.integrations.testcontainers.{
  WireMockContainer => JavaWireMockContainer
}
import org.testcontainers.utility.DockerImageName

class WireMockContainer(underlying: JavaWireMockContainer)
    extends SingleContainer[JavaWireMockContainer] {
  override val container: JavaWireMockContainer = underlying

  def getHost: String = container.getHost
  def getPort: Int = container.getPort
  def getBaseUrl: String = container.getBaseUrl
  def getUrl(path: String): String = container.getUrl(path)
}

object WireMockContainer {
  val defaultImage: String = "wiremock/wiremock"
  val defaultTag: String = "latest"
  val defaultDockerImageName: String = s"$defaultImage:$defaultTag"

  case class Def(
      dockerImageName: DockerImageName =
        DockerImageName.parse(defaultDockerImageName),
      builderFs: Seq[JavaWireMockContainer => JavaWireMockContainer] =
        Seq.empty[JavaWireMockContainer => JavaWireMockContainer]
  ) extends ContainerDef {
    override type Container = WireMockContainer

    def withMappingFromResource(resourceName: String): Def = {
      copy(builderFs =
        builderFs :+ ((underlying: JavaWireMockContainer) =>
          underlying.withMappingFromResource(resourceName)
        )
      )
    }

    def withFileFromResource(resourceName: String): Def = {
      copy(builderFs =
        builderFs :+ ((underlying: JavaWireMockContainer) =>
          underlying.withFileFromResource(resourceName)
        )
      )
    }

    override protected def createContainer(): WireMockContainer = {
      val underlying =
        builderFs.foldLeft(new JavaWireMockContainer(dockerImageName))(
          (underlying, f) => f(underlying)
        )

      new WireMockContainer(underlying)
    }
  }
}
