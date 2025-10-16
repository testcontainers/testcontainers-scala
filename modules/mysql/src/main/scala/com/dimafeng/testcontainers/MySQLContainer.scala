package com.dimafeng.testcontainers

import org.testcontainers.mysql.{MySQLContainer => JavaMySQLContainer}
import org.testcontainers.utility.DockerImageName

class MySQLContainer(
  configurationOverride: Option[String] = None,
  mysqlImageVersion: Option[DockerImageName] = None,
  databaseName: Option[String] = None,
  mysqlUsername: Option[String] = None,
  mysqlPassword: Option[String] = None,
  urlParams: Map[String, String] = Map.empty,
  commonJdbcParams: JdbcDatabaseContainer.CommonParams = JdbcDatabaseContainer.CommonParams()
) extends SingleContainer[JavaMySQLContainer] with JdbcDatabaseContainer {

  override val container: JavaMySQLContainer = {
    val c: JavaMySQLContainer = mysqlImageVersion
      .map(new JavaMySQLContainer(_))
      .getOrElse(new JavaMySQLContainer(MySQLContainer.DEFAULT_MYSQL_VERSION))

    databaseName.map(c.withDatabaseName)
    mysqlUsername.map(c.withUsername)
    mysqlPassword.map(c.withPassword)

    configurationOverride.foreach(c.withConfigurationOverride)
    urlParams.foreach { case (key, value) =>
      c.withUrlParam(key, value)
    }

    commonJdbcParams.applyTo(c)

    c
  }

  def testQueryString: String = container.getTestQueryString

}

object MySQLContainer {

  val defaultDockerImageName = s"${JavaMySQLContainer.IMAGE}:${JavaMySQLContainer.DEFAULT_TAG}"
  val defaultDatabaseName = "test"
  val defaultUsername = "test"
  val defaultPassword = "test"

  val DEFAULT_MYSQL_VERSION = defaultDockerImageName

  def apply(
    configurationOverride: String = null,
    mysqlImageVersion: DockerImageName = null,
    databaseName: String = null,
    username: String = null,
    password: String = null
  ): MySQLContainer = {
    new MySQLContainer(
      Option(configurationOverride),
      Option(mysqlImageVersion),
      Option(databaseName),
      Option(username),
      Option(password)
    )
  }

  case class Def(
    dockerImageName: DockerImageName = DockerImageName.parse(defaultDockerImageName),
    databaseName: String = defaultDatabaseName,
    username: String = defaultUsername,
    password: String = defaultPassword,
    configurationOverride: Option[String] = None,
    urlParams: Map[String, String] = Map.empty,
    commonJdbcParams: JdbcDatabaseContainer.CommonParams = JdbcDatabaseContainer.CommonParams()
  ) extends ContainerDef {

    override type Container = MySQLContainer

    override def createContainer(): MySQLContainer = {
      new MySQLContainer(
        mysqlImageVersion = Some(dockerImageName),
        databaseName = Some(databaseName),
        mysqlUsername = Some(username),
        mysqlPassword = Some(password),
        configurationOverride = configurationOverride,
        urlParams = urlParams,
        commonJdbcParams = commonJdbcParams
      )
    }
  }

}
