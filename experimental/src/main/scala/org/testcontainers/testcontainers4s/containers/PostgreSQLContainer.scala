package org.testcontainers.testcontainers4s.containers

import org.testcontainers.containers.{PostgreSQLContainer => JavaPostgreSQLContainer}
import org.testcontainers.testcontainers4s.containers.PostgreSQLContainer.{PostgreSQLContainerRuntime, defaultDatabaseName, defaultDockerImageName, defaultPassword, defaultUsername}

object PostgreSQLContainer {

  val defaultDockerImageName = s"${JavaPostgreSQLContainer.IMAGE}:${JavaPostgreSQLContainer.DEFAULT_TAG}"
  val defaultDatabaseName = "test"
  val defaultUsername = "test"
  val defaultPassword = "test"

  class PostgreSQLContainerRuntime private[containers] (
                                                  val underlyingUnsafeContainer: JavaPostgreSQLContainer[_]
                                                ) extends ContainerRuntime with JdbcDatabaseContainer[JavaPostgreSQLContainer[_]] {
    override type JavaContainer = JavaPostgreSQLContainer[_]
  }
}

// TODO: add extraConfiguration with java container?
case class PostgreSQLContainer(
                dockerImageName: String = defaultDockerImageName,
                databaseName: String = defaultDatabaseName,
                username: String = defaultUsername,
                password: String = defaultPassword,
              ) extends Container {

  override type Container = PostgreSQLContainerRuntime

  override def createContainer(): PostgreSQLContainerRuntime = {
    val javaContainer = new JavaPostgreSQLContainer(dockerImageName)
    javaContainer.withDatabaseName(databaseName)
    javaContainer.withPassword(password)
    javaContainer.withUsername(username)
    new PostgreSQLContainerRuntime(javaContainer)
  }
}