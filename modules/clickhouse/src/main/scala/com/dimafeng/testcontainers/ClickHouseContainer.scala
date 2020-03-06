package com.dimafeng.testcontainers

import org.testcontainers.containers.{ClickHouseContainer => JavaClickHouseContainer}

case class ClickHouseContainer(
  dockerImageName: String = ClickHouseContainer.defaultDockerImageName
) extends SingleContainer[JavaClickHouseContainer] with JdbcDatabaseContainer {

  override val container: JavaClickHouseContainer = new JavaClickHouseContainer(dockerImageName)

  def testQueryString: String = container.getTestQueryString
}

object ClickHouseContainer {

  val defaultDockerImageName = s"${JavaClickHouseContainer.IMAGE}:${JavaClickHouseContainer.DEFAULT_TAG}"

  case class Def(
    dockerImageName: String = ClickHouseContainer.defaultDockerImageName
  ) extends ContainerDef {

    override type Container = ClickHouseContainer

    override def createContainer(): ClickHouseContainer = {
      new ClickHouseContainer(
        dockerImageName = dockerImageName
      )
    }
  }
}
