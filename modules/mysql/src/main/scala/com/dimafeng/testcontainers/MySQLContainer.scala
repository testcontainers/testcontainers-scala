package com.dimafeng.testcontainers

import org.testcontainers.containers.{MySQLContainer => JavaMySQLContainer}

class MySQLContainer(configurationOverride: Option[String] = None,
                     mysqlImageVersion: Option[String] = None,
                     databaseName: Option[String] = None,
                     mysqlUsername: Option[String] = None,
                     mysqlPassword: Option[String] = None)
  extends SingleContainer[JavaMySQLContainer[_]] with JdbcDatabaseContainer {

  override val container: JavaMySQLContainer[_] = mysqlImageVersion
    .map(new JavaMySQLContainer(_))
    .getOrElse(new JavaMySQLContainer(MySQLContainer.DEFAULT_MYSQL_VERSION))

  databaseName.map(container.withDatabaseName)
  mysqlUsername.map(container.withUsername)
  mysqlPassword.map(container.withPassword)

  configurationOverride.foreach(container.withConfigurationOverride)

  def testQueryString: String = container.getTestQueryString

}

object MySQLContainer {

  val defaultDockerImageName = s"${JavaMySQLContainer.IMAGE}:${JavaMySQLContainer.DEFAULT_TAG}"
  val defaultDatabaseName = "test"
  val defaultUsername = "test"
  val defaultPassword = "test"

  val DEFAULT_MYSQL_VERSION = defaultDockerImageName

  def apply(configurationOverride: String = null,
            mysqlImageVersion: String = null,
            databaseName: String = null,
            username: String = null,
            password: String = null): MySQLContainer =
    new MySQLContainer(Option(configurationOverride),
      Option(mysqlImageVersion),
      Option(databaseName),
      Option(username),
      Option(password))

  case class Def(
    dockerImageName: String = defaultDockerImageName,
    databaseName: String = defaultDatabaseName,
    username: String = defaultUsername,
    password: String = defaultPassword,
    configurationOverride: Option[String] = None
  ) extends ContainerDef {

    override type Container = MySQLContainer

    override def createContainer(): MySQLContainer = {
      new MySQLContainer(
        mysqlImageVersion = Some(dockerImageName),
        databaseName = Some(databaseName),
        mysqlUsername = Some(username),
        mysqlPassword = Some(password),
        configurationOverride = configurationOverride
      )
    }
  }

}
