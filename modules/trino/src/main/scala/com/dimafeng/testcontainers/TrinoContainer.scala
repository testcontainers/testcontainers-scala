package com.dimafeng.testcontainers

import java.sql.Connection

import org.testcontainers.containers.{TrinoContainer => JavaTrinoContainer}
import org.testcontainers.utility.DockerImageName

case class TrinoContainer(
  dockerImageName: DockerImageName = DockerImageName.parse(TrinoContainer.defaultDockerImageName),
  dbUsername: String = TrinoContainer.defaultDbUsername,
  dbName: String = TrinoContainer.defaultDbName,
  commonJdbcParams: JdbcDatabaseContainer.CommonParams = JdbcDatabaseContainer.CommonParams()
) extends SingleContainer[JavaTrinoContainer] with JdbcDatabaseContainer {

  override val container: JavaTrinoContainer = {
    val c: JavaTrinoContainer = new JavaTrinoContainer(dockerImageName)
    c.withUsername(dbUsername)
    c.withDatabaseName(dbName)
    commonJdbcParams.applyTo(c)
    c
  }

  def testQueryString: String = container.getTestQueryString

  def createConnection: Connection = container.createConnection()
}

object TrinoContainer {

  val defaultImage = "trinodb/trino"
  val defaultTag = "352"
  val defaultDockerImageName = s"$defaultImage:$defaultTag"
  val defaultDbUsername = "test"
  val defaultDbName = ""

  case class Def(
    dockerImageName: DockerImageName = DockerImageName.parse(TrinoContainer.defaultDockerImageName),
    dbUsername: String = TrinoContainer.defaultDbUsername,
    dbName: String = TrinoContainer.defaultDbName,
    commonJdbcParams: JdbcDatabaseContainer.CommonParams = JdbcDatabaseContainer.CommonParams()
  ) extends ContainerDef {

    override type Container = TrinoContainer

    override def createContainer(): TrinoContainer = {
      new TrinoContainer(
        dockerImageName,
        dbUsername,
        dbName,
        commonJdbcParams
      )
    }
  }
}
