package com.dimafeng.testcontainers

import org.testcontainers.containers.{MockServerContainer => JavaMockServerContainer}

case class MockServerContainer(
  version: String = MockServerContainer.defaultVersion
) extends SingleContainer[JavaMockServerContainer] {

  override val container: JavaMockServerContainer = new JavaMockServerContainer(version)

  def endpoint: String = container.getEndpoint

  def serverPort: Int = container.getServerPort
}

object MockServerContainer {

  val defaultVersion = JavaMockServerContainer.VERSION

  case class Def(
    version: String = MockServerContainer.defaultVersion
  ) extends ContainerDef {

    override type Container = MockServerContainer

    override def createContainer(): MockServerContainer = {
      new MockServerContainer(
        version
      )
    }
  }

}
