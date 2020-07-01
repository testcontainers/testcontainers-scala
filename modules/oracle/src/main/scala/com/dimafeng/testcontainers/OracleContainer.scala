package com.dimafeng.testcontainers

import org.testcontainers.containers.{OracleContainer => JavaOracleContainer}

import scala.concurrent.duration._

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
  containerSharedMemory: Long = OracleContainer.defaultSharedMemory,
  commonJdbcParams: JdbcDatabaseContainer.CommonParams = OracleContainer.defaultCommonJdbcParams
) extends SingleContainer[JavaOracleContainer] {

  override val container: JavaOracleContainer = {
    val c = new JavaOracleContainer(dockerImageName)
      .withSharedMemorySize(containerSharedMemory)
      .withUsername(oraUsername)
      .withPassword(oraPassword)
    commonJdbcParams.applyTo(c)
    c
  }

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
  val defaultCommonJdbcParams: JdbcDatabaseContainer.CommonParams = JdbcDatabaseContainer.CommonParams().copy(
    startupTimeout = 240.seconds
  )

  case class Def(
    dockerImageName: String,
    username: String = defaultUsername,
    password: String = defaultPassword,
    containerSharedMemory: Long = defaultSharedMemory,
    commonJdbcParams: JdbcDatabaseContainer.CommonParams = OracleContainer.defaultCommonJdbcParams
  ) extends ContainerDef {

    override type Container = OracleContainer

    override def createContainer(): OracleContainer = {
      OracleContainer(
        dockerImageName = dockerImageName,
        oraUsername = username,
        oraPassword = password,
        containerSharedMemory = containerSharedMemory,
        commonJdbcParams = commonJdbcParams,
      )
    }
  }
}
