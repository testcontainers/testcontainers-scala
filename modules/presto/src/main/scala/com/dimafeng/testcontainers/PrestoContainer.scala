package com.dimafeng.testcontainers

import java.sql.Connection

import org.testcontainers.containers.{PrestoContainer => JavaPrestoContainer}

case class PrestoContainer(
  dockerImageName: String = PrestoContainer.defaultDockerImageName,
  dbUsername: String = PrestoContainer.defaultDbUsername,
  dbName: String = PrestoContainer.defaultDbName
) extends SingleContainer[JavaPrestoContainer[_]] with JdbcDatabaseContainer {

  override val container: JavaPrestoContainer[_] = {
    val c = new JavaPrestoContainer(dockerImageName)
    c.withUsername(dbUsername)
    c.withDatabaseName(dbName)
    c
  }

  def testQueryString: String = container.getTestQueryString

  def createConnection: Connection = container.createConnection()
}

object PrestoContainer {

  val defaultDockerImageName = s"${JavaPrestoContainer.IMAGE}:${JavaPrestoContainer.DEFAULT_TAG}"
  val defaultDbUsername = "test"
  val defaultDbName = ""

  case class Def(
    dockerImageName: String = PrestoContainer.defaultDockerImageName,
    dbUsername: String = PrestoContainer.defaultDbUsername,
    dbName: String = PrestoContainer.defaultDbName
  ) extends ContainerDef {

    override type Container = PrestoContainer

    override def createContainer(): PrestoContainer = {
      new PrestoContainer(
        dockerImageName,
        dbUsername,
        dbName
      )
    }
  }
}
