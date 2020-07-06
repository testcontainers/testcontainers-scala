package com.dimafeng.testcontainers

import org.testcontainers.containers.{MSSQLServerContainer => JavaMSSQLServerContainer}

case class MSSQLServerContainer(
  dockerImageName: String = MSSQLServerContainer.defaultDockerImageName,
  dbPassword: String = MSSQLServerContainer.defaultPassword
) extends SingleContainer[JavaMSSQLServerContainer[_]] with JdbcDatabaseContainer {

  override val container: JavaMSSQLServerContainer[_] = {
    val c = new JavaMSSQLServerContainer(dockerImageName)
    c.withPassword(dbPassword)
    c
  }

  def testQueryString: String = container.getTestQueryString
}

object MSSQLServerContainer {

  val defaultDockerImageName = s"${JavaMSSQLServerContainer.IMAGE}:${JavaMSSQLServerContainer.DEFAULT_TAG}"
  val defaultPassword = "A_Str0ng_Required_Password"

  case class Def(
    dockerImageName: String = MSSQLServerContainer.defaultDockerImageName,
    password: String = MSSQLServerContainer.defaultPassword
  ) extends ContainerDef {

    override type Container = MSSQLServerContainer

    override def createContainer(): MSSQLServerContainer = {
      new MSSQLServerContainer(
        dockerImageName = dockerImageName,
        dbPassword = password
      )
    }
  }
}
