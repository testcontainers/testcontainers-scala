package com.dimafeng.testcontainers

import org.testcontainers.containers.{ClickHouseContainer => JavaClickHouseContainer}

case class ClickHouseContainer(
  dockerImageName: String = ClickHouseContainer.defaultDockerImageName,
  dbPassword: String = ClickHouseContainer.defaultPassword
) extends SingleContainer[JavaClickHouseContainer] with JdbcDatabaseContainer {

  override val container: JavaClickHouseContainer = {
    val c = new JavaClickHouseContainer(dockerImageName)
    c.withPassword(dbPassword)
    c
  }

  def testQueryString: String = container.getTestQueryString
}

object ClickHouseContainer {

  val defaultDockerImageName = s"${JavaClickHouseContainer.IMAGE}:${JavaClickHouseContainer.DEFAULT_TAG}"
  val defaultPassword = ""

  case class Def(
    dockerImageName: String = ClickHouseContainer.defaultDockerImageName,
    password: String = ClickHouseContainer.defaultPassword
  ) extends ContainerDef {

    override type Container = ClickHouseContainer

    override def createContainer(): ClickHouseContainer = {
      new ClickHouseContainer(
        dockerImageName = dockerImageName,
        dbPassword = password
      )
    }
  }
}
