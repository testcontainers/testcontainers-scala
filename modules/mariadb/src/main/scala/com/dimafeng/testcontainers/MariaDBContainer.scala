package com.dimafeng.testcontainers

import org.testcontainers.containers.{MariaDBContainer => JavaMariaDBContainer}

case class MariaDBContainer(
  dockerImageName: String = MariaDBContainer.defaultDockerImageName,
  dbName: String = MariaDBContainer.defaultDatabaseName,
  dbUsername: String = MariaDBContainer.defaultUsername,
  dbPassword: String = MariaDBContainer.defaultPassword,
  configurationOverride: Option[String] = None
) extends SingleContainer[JavaMariaDBContainer[_]] with JdbcDatabaseContainer {

  override val container: JavaMariaDBContainer[_] = {
    val c = new JavaMariaDBContainer(dockerImageName)
    c.withDatabaseName(databaseName)
    c.withUsername(username)
    c.withPassword(password)
    configurationOverride.foreach(c.withConfigurationOverride)
    c
  }

  def testQueryString: String = container.getTestQueryString

}

object MariaDBContainer {

  val defaultDockerImageName = s"${JavaMariaDBContainer.IMAGE}:${JavaMariaDBContainer.DEFAULT_TAG}"
  val defaultDatabaseName = "test"
  val defaultUsername = "test"
  val defaultPassword = "test"

  case class Def(
    dockerImageName: String = MariaDBContainer.defaultDockerImageName,
    dbName: String = MariaDBContainer.defaultDatabaseName,
    dbUsername: String = MariaDBContainer.defaultUsername,
    dbPassword: String = MariaDBContainer.defaultPassword,
    configurationOverride: Option[String] = None
  ) extends ContainerDef {

    override type Container = MariaDBContainer

    override def createContainer(): MariaDBContainer = {
      new MariaDBContainer(
        dockerImageName,
        dbName,
        dbUsername,
        dbPassword,
        configurationOverride
      )
    }
  }

}
