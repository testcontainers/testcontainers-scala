package org.testcontainers.testcontainers4s.containers

import java.sql.Driver

import org.testcontainers.containers.{MySQLContainer => JavaMySQLContainer}
import org.testcontainers.testcontainers4s.containers.MySQLContainer.{MySQLContainerRuntime, defaultDatabaseName, defaultDockerImageName, defaultPassword, defaultUsername}

object MySQLContainer {

  val defaultDockerImageName = s"${JavaMySQLContainer.IMAGE}:${JavaMySQLContainer.DEFAULT_TAG}"
  val defaultDatabaseName = "test"
  val defaultUsername = "test"
  val defaultPassword = "test"

  class MySQLContainerRuntime private[containers] (
                                                    val underlyingUnsafeContainer: JavaMySQLContainer[_]
                                                  ) extends ContainerRuntime with JdbcDatabaseContainer[JavaMySQLContainer[_]] {
    override type JavaContainer = JavaMySQLContainer[_]
  }
}
case class MySQLContainer(
                dockerImageName: String = defaultDockerImageName,
                databaseName: String = defaultDatabaseName,
                username: String = defaultUsername,
                password: String = defaultPassword,
                configurationOverride: Option[String] = None,
              ) extends Container {

  override type Container = MySQLContainerRuntime

  override def createContainer(): MySQLContainerRuntime = {
    val javaContainer = new JavaMySQLContainer(dockerImageName)
    javaContainer.withDatabaseName(databaseName)
    javaContainer.withPassword(password)
    javaContainer.withUsername(username)
    configurationOverride.foreach(javaContainer.withConfigurationOverride)
    new MySQLContainerRuntime(javaContainer)
  }

  @deprecated def jdbcUrl(implicit c: Container): String = c.jdbcUrl
}
