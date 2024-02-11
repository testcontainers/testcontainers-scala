package com.dimafeng.testcontainers

import org.testcontainers.containers.{MockServerContainer => JavaMockServerContainer}
import org.testcontainers.utility.DockerImageName

case class MockServerContainer(
  version: String = MockServerContainer.defaultVersion
) extends SingleContainer[JavaMockServerContainer] {

  override val container: JavaMockServerContainer =
    new JavaMockServerContainer(
      MockServerContainer.defaultImageName.withTag(s"mockserver-$version")
    )

  def endpoint: String = container.getEndpoint

  def serverPort: Int = container.getServerPort

  def serverHost: String = container.getHost
}

object MockServerContainer {

  private[testcontainers] final val defaultImageName = DockerImageName.parse("mockserver/mockserver")

  private[testcontainers] final val defaultVersion = "5.13.2"

  case class Def(
    version: String = defaultVersion
  ) extends ContainerDef {

    override type Container = MockServerContainer

    override def createContainer(): MockServerContainer = {
      new MockServerContainer(
        version
      )
    }
  }

}
