package com.dimafeng.testcontainers

import org.testcontainers.containers.{MSSQLServerContainer => JavaMSSQLServerContainer}
import org.testcontainers.utility.DockerImageName

import scala.concurrent.duration._

case class MSSQLServerContainer(
  dockerImageName: DockerImageName = DockerImageName.parse(MSSQLServerContainer.defaultDockerImageName),
  dbPassword: String = MSSQLServerContainer.defaultPassword,
  urlParams: Map[String, String] = Map.empty,
  commonJdbcParams: JdbcDatabaseContainer.CommonParams = MSSQLServerContainer.defaultCommonJdbcParams
) extends SingleContainer[JavaMSSQLServerContainer[?]] with JdbcDatabaseContainer {

  override val container: JavaMSSQLServerContainer[?] = {
    val c: JavaMSSQLServerContainer[?] = new JavaMSSQLServerContainer(dockerImageName)

    c.withPassword(dbPassword)
    urlParams.foreach { case (key, value) =>
      c.withUrlParam(key, value)
    }
    commonJdbcParams.applyTo(c)

    c
  }

  def testQueryString: String = container.getTestQueryString
}

object MSSQLServerContainer {

  val defaultDockerImageName = s"${JavaMSSQLServerContainer.IMAGE}:${JavaMSSQLServerContainer.DEFAULT_TAG}"
  val defaultPassword = "A_Str0ng_Required_Password"
  val defaultCommonJdbcParams: JdbcDatabaseContainer.CommonParams = JdbcDatabaseContainer.CommonParams().copy(
    startupTimeout = 240.seconds,
    connectTimeout = 240.seconds
  )

  case class Def(
    dockerImageName: DockerImageName = DockerImageName.parse(MSSQLServerContainer.defaultDockerImageName),
    password: String = MSSQLServerContainer.defaultPassword,
    urlParams: Map[String, String] = Map.empty,
    commonJdbcParams: JdbcDatabaseContainer.CommonParams = MSSQLServerContainer.defaultCommonJdbcParams
  ) extends ContainerDef {

    override type Container = MSSQLServerContainer

    override def createContainer(): MSSQLServerContainer = {
      new MSSQLServerContainer(
        dockerImageName = dockerImageName,
        dbPassword = password,
        urlParams = urlParams,
        commonJdbcParams = commonJdbcParams
      )
    }
  }
}
