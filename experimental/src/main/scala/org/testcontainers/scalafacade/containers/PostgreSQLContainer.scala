package org.testcontainers.scalafacade.containers

import org.testcontainers.containers.{PostgreSQLContainer => JavaPostgreSQLContainer}

object PostgreSQLContainer {

  val defaultDockerImageName = s"${JavaPostgreSQLContainer.IMAGE}:${JavaPostgreSQLContainer.DEFAULT_TAG}"
  val defaultDatabaseName = "test"
  val defaultUsername = "test"
  val defaultPassword = "test"

  class Def(
    dockerImageName: String = defaultDockerImageName,
    databaseName: String = defaultDatabaseName,
    username: String = defaultUsername,
    password: String = defaultPassword,
  ) extends ContainerDef[JavaPostgreSQLContainer[_], PostgreSQLContainer] {

    override def createContainer: PostgreSQLContainer = {
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
) extends Container[JavaPostgreSQLContainer[_]] {

  def driverClassName: String = underlyingUnsafeContainer.getDriverClassName

  def jdbcUrl: String = underlyingUnsafeContainer.getJdbcUrl

  def databaseName: String = underlyingUnsafeContainer.getDatabaseName

  def username: String = underlyingUnsafeContainer.getUsername

  def password: String = underlyingUnsafeContainer.getPassword

  def testQueryString: String = underlyingUnsafeContainer.getTestQueryString
}
