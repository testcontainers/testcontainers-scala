package com.dimafeng.testcontainers

import org.testcontainers.clickhouse.{ClickHouseContainer => JavaClickHouseContainer}
import org.testcontainers.utility.DockerImageName

case class ClickHouseContainer(
  dockerImageName: DockerImageName = DockerImageName.parse(ClickHouseContainer.defaultDockerImageName),
  clickhouseDatabaseName: Option[String] = None,
  clickhouseUsername: Option[String] = None,
  clickhousePassword: Option[String] = None
) extends SingleContainer[JavaClickHouseContainer] with JdbcDatabaseContainer {

  override val container: JavaClickHouseContainer = {
    val c = new JavaClickHouseContainer(dockerImageName)
    clickhouseDatabaseName.map(c.withDatabaseName)
    clickhouseUsername.map(c.withUsername)
    clickhousePassword.map(c.withPassword)
    c
  }

  def testQueryString: String = container.getTestQueryString
}

object ClickHouseContainer {

  // Copy String literal because JavaClickHouseContainer.CLICKHOUSE_IMAGE_NAME is private
  val defaultDockerImageName = "clickhouse/clickhouse-server"
  val defaultDatabaseName = "test"
  val defaultUsername = "test"
  val defaultPassword = "test"

  case class Def(
    dockerImageName: DockerImageName = DockerImageName.parse(defaultDockerImageName),
    databaseName: String = defaultDatabaseName,
    username: String = defaultUsername,
    password: String = defaultPassword,
  ) extends ContainerDef {

    override type Container = ClickHouseContainer

    override def createContainer(): ClickHouseContainer = {
      new ClickHouseContainer(
        dockerImageName = dockerImageName,
        clickhouseDatabaseName = Some(databaseName),
        clickhouseUsername = Some(username),
        clickhousePassword = Some(password)
      )
    }
  }
}
