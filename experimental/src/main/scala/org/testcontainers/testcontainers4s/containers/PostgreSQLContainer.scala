package org.testcontainers.testcontainers4s.containers

import org.testcontainers.containers.{PostgreSQLContainer => JavaPostgreSQLContainer}

object PostgreSQLContainer {

  val defaultDockerImageName = s"${JavaPostgreSQLContainer.IMAGE}:${JavaPostgreSQLContainer.DEFAULT_TAG}"
  val defaultDatabaseName = "test"
  val defaultUsername = "test"
  val defaultPassword = "test"

  // TODO: add extraConfiguration with java container?
  case class Def(
    dockerImageName: String = defaultDockerImageName,
    databaseName: String = defaultDatabaseName,
    username: String = defaultUsername,
    password: String = defaultPassword,
  ) extends ContainerDef {

    override type Container = PostgreSQLContainer

    override def createContainer(): PostgreSQLContainer = {
      val javaContainer = new JavaPostgreSQLContainer(dockerImageName)
      javaContainer.withDatabaseName(databaseName)
      javaContainer.withPassword(password)
      javaContainer.withUsername(username)
      new PostgreSQLContainer(javaContainer)
    }
  }
}

class PostgreSQLContainer private[containers] (
  val underlyingUnsafeContainer: JavaPostgreSQLContainer[_]
) extends Container with JdbcDatabaseContainer[JavaPostgreSQLContainer[_]] {
  override type JavaContainer = JavaPostgreSQLContainer[_]
}
