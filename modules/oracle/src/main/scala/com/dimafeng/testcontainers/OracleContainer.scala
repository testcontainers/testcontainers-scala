package com.dimafeng.testcontainers

import org.testcontainers.containers.{OracleContainer => JavaOracleContainer}

class OracleContainer(
  dockerImageName: String = OracleContainer.defaultDockerImageName,
  databaseName: String = OracleContainer.defaultDatabaseName,
  oraUsername: String = OracleContainer.defaultUsername,
  oraPassword: String = OracleContainer.defaultPassword,
  containerSharedMemory: Long = OracleContainer.defaultSharedMemory
) extends SingleContainer[JavaOracleContainer] {

  override val container: JavaOracleContainer =
    new JavaOracleContainer(dockerImageName)
      .withSharedMemorySize(containerSharedMemory)
      .withDatabaseName(databaseName)
      .withUsername(oraUsername)
      .withPassword(oraPassword)

  def driverClassName: String = container.getDriverClassName

  def jdbcUrl: String = container.getJdbcUrl

  def username: String = container.getUsername

  def password: String = container.getPassword

  def testQueryString: String = container.getTestQueryString
}

object OracleContainer {

  val defaultDockerImageName = s"oracleinanutshell/oracle-xe-11g"
  val defaultDatabaseName = "xe"
  val defaultUsername = "system"
  val defaultPassword = "oracle"
  val defaultSharedMemory = 10240000000L //1 GB

  case class Def(dockerImageName: String = defaultDockerImageName,
                 databaseName: String = defaultDatabaseName,
                 username: String = defaultUsername,
                 password: String = defaultPassword,
                 containerSharedMemory: Long = defaultSharedMemory)
      extends ContainerDef {

    override type Container = OracleContainer

    override def createContainer(): OracleContainer = {
      new OracleContainer(
        dockerImageName = dockerImageName,
        databaseName = databaseName,
        oraUsername = username,
        oraPassword = password,
        containerSharedMemory = containerSharedMemory
      )
    }
  }
}
