package org.testcontainers.testcontainers4s.containers

import org.testcontainers.containers.{MySQLContainer => JavaMySQLContainer}

object MySQLContainer {

  val defaultDockerImageName = s"${JavaMySQLContainer.IMAGE}:${JavaMySQLContainer.DEFAULT_TAG}"
  val defaultDatabaseName = "test"
  val defaultUsername = "test"
  val defaultPassword = "test"

  class Def(
    dockerImageName: String = defaultDockerImageName,
    databaseName: String = defaultDatabaseName,
    username: String = defaultUsername,
    password: String = defaultPassword,
    configurationOverride: Option[String] = None,
  ) extends ContainerDef[JavaMySQLContainer[_], MySQLContainer] {

    override def createContainer: MySQLContainer = {
      val javaContainer = new JavaMySQLContainer(dockerImageName)
      javaContainer.withDatabaseName(databaseName)
      javaContainer.withPassword(password)
      javaContainer.withUsername(username)
      configurationOverride.foreach(javaContainer.withConfigurationOverride)
      new MySQLContainer(javaContainer)
    }
  }
}

class MySQLContainer private[containers] (
  val underlyingUnsafeContainer: JavaMySQLContainer[_]
) extends Container[JavaMySQLContainer[_]] with JdbcDatabaseContainer[JavaMySQLContainer[_]]
