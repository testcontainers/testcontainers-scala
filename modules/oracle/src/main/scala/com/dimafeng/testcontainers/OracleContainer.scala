package com.dimafeng.testcontainers

import org.testcontainers.containers.{OracleContainer => JavaOracleContainer}

/**
  * @param dockerImageName Oracle doesn't have any official distribution of XE,
  *                        so we don't provide any default `dockerImageName`.
  *                        You either need to build your own image or use some third-party image,
  *                        for instance "oracleinanutshell/oracle-xe-11g".
  */
case class OracleContainer(
  dockerImageName: String,
  oraUsername: String = OracleContainer.defaultUsername,
  oraPassword: String = OracleContainer.defaultPassword,
  containerSharedMemory: Long = OracleContainer.defaultSharedMemory
) extends SingleContainer[JavaOracleContainer] {

  override val container: JavaOracleContainer =
    new JavaOracleContainer(dockerImageName)
      .withSharedMemorySize(containerSharedMemory)
      .withUsername(oraUsername)
      .withPassword(oraPassword)

  def driverClassName: String = container.getDriverClassName

  def jdbcUrl: String = container.getJdbcUrl

  def username: String = container.getUsername

  def password: String = container.getPassword

  def testQueryString: String = container.getTestQueryString
}

object OracleContainer {

  val defaultDatabaseName = "xe"
  val defaultUsername = "system"
  val defaultPassword = "oracle"
  val defaultSharedMemory = 10240000000L //1 GB

  case class Def(dockerImageName: String,
                 username: String = defaultUsername,
                 password: String = defaultPassword,
                 containerSharedMemory: Long = defaultSharedMemory)
      extends ContainerDef {

    override type Container = OracleContainer

    override def createContainer(): OracleContainer = {
      OracleContainer(
        dockerImageName = dockerImageName,
        oraUsername = username,
        oraPassword = password,
        containerSharedMemory = containerSharedMemory
      )
    }
  }
}
