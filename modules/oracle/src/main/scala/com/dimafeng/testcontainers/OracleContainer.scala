package com.dimafeng.testcontainers

import com.dimafeng.testcontainers.{ContainerDef, SingleContainer}
import org.testcontainers.containers.{OracleContainer => JavaOracleContainer}

class OracleContainer(dockerImageNameOverride: Option[String] = None,
                      databaseName: Option[String] = None,
                      oraUsername: Option[String] = None,
                      oraPassword: Option[String] = None,
                      containerSharedMemory: Long = OracleContainer.defaultSharedMemory)
  extends SingleContainer[JavaOracleContainer] {

  override val container: JavaOracleContainer = dockerImageNameOverride match {
    case Some(imageNameOverride) =>
      new JavaOracleContainer(imageNameOverride)
    case None =>
      new JavaOracleContainer()
  }

  container.withSharedMemorySize(containerSharedMemory)
  databaseName.foreach(container.withDatabaseName)
  oraUsername.foreach(container.withUsername)
  oraPassword.foreach(container.withPassword)

  def driverClassName: String = container.getDriverClassName

  def jdbcUrl: String = container.getJdbcUrl

  def username: String = container.getUsername

  def password: String = container.getPassword

  def testQueryString: String = container.getTestQueryString
}

object OracleContainer {

  val defaultDockerImageName = s"oracleinanutshell/oracle-xe-11g"
  val defaultDatabaseName    = "xe"
  val defaultUsername        = "system"
  val defaultPassword        = "oracle"
  val defaultSharedMemory    = 10240000000L //1 GB

  def apply(dockerImageNameOverride: String = null,
            databaseName: String = null,
            username: String = null,
            password: String = null): OracleContainer =
    new OracleContainer(Option(dockerImageNameOverride),
      Option(databaseName),
      Option(username),
      Option(password),
      OracleContainer.defaultSharedMemory)

  case class Def(dockerImageName: String = defaultDockerImageName,
                 databaseName: String = defaultDatabaseName,
                 username: String = defaultUsername,
                 password: String = defaultPassword,
                 containerSharedMemory: Long = defaultSharedMemory)
    extends ContainerDef {

    override type Container = OracleContainer

    override def createContainer(): OracleContainer = {
      new OracleContainer(
        dockerImageNameOverride = Some(dockerImageName),
        databaseName = Some(databaseName),
        oraUsername = Some(username),
        oraPassword = Some(password),
        containerSharedMemory = containerSharedMemory
      )
    }
  }
}
