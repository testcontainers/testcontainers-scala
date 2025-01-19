package com.dimafeng.testcontainers

import java.sql.Connection

import org.testcontainers.containers.{PrestoContainer => JavaPrestoContainer}
import org.testcontainers.utility.DockerImageName

case class PrestoContainer(
  dockerImageName: DockerImageName = DockerImageName.parse(PrestoContainer.defaultDockerImageName),
  dbUsername: String = PrestoContainer.defaultDbUsername,
  dbName: String = PrestoContainer.defaultDbName,
  commonJdbcParams: JdbcDatabaseContainer.CommonParams = JdbcDatabaseContainer.CommonParams()
) extends SingleContainer[JavaPrestoContainer[?]] with JdbcDatabaseContainer {

  override val container: JavaPrestoContainer[?] = {
    val c: JavaPrestoContainer[?] = new JavaPrestoContainer(dockerImageName)
    c.withUsername(dbUsername)
    c.withDatabaseName(dbName)
    commonJdbcParams.applyTo(c)
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
    dockerImageName: DockerImageName = DockerImageName.parse(PrestoContainer.defaultDockerImageName),
    dbUsername: String = PrestoContainer.defaultDbUsername,
    dbName: String = PrestoContainer.defaultDbName,
    commonJdbcParams: JdbcDatabaseContainer.CommonParams = JdbcDatabaseContainer.CommonParams()
  ) extends ContainerDef {

    override type Container = PrestoContainer

    override def createContainer(): PrestoContainer = {
      new PrestoContainer(
        dockerImageName,
        dbUsername,
        dbName,
        commonJdbcParams
      )
    }
  }
}
