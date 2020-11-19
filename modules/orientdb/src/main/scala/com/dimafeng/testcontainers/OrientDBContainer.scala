package com.dimafeng.testcontainers

import com.orientechnologies.orient.core.db.{ODatabaseSession, OrientDB}
import org.testcontainers.containers.{OrientDBContainer => JavaOrientDBContainer}
import org.testcontainers.utility.DockerImageName

case class OrientDBContainer(
  dockerImageName: DockerImageName = DockerImageName.parse(OrientDBContainer.defaultDockerImageName),
  databaseName: String = OrientDBContainer.defaultDatabaseName,
  serverPassword: String = OrientDBContainer.defaultServerPassword,
  scriptPath: Option[String] = None
) extends SingleContainer[JavaOrientDBContainer] {

  override val container: JavaOrientDBContainer = {
    val c = new JavaOrientDBContainer(dockerImageName)
    c.withDatabaseName(databaseName)
    c.withServerPassword(serverPassword)
    scriptPath.foreach(c.withScriptPath)
    c
  }

  def testQueryString: String = container.getTestQueryString

  def orientDB: OrientDB = container.getOrientDB

  def serverUrl: String = container.getServerUrl

  def dbUrl: String = container.getDbUrl

  def session: ODatabaseSession = container.getSession

  def session(username: String, password: String): ODatabaseSession = container.getSession(username, password)
}

object OrientDBContainer {

  val defaultDockerImageName = "orientdb:3.0.24-tp3"
  val defaultDatabaseName = "testcontainers"
  val defaultServerPassword = "root"

  case class Def(
    dockerImageName: DockerImageName = DockerImageName.parse(OrientDBContainer.defaultDockerImageName),
    databaseName: String = OrientDBContainer.defaultDatabaseName,
    serverPassword: String = OrientDBContainer.defaultServerPassword,
    scriptPath: Option[String] = None
  ) extends ContainerDef {

    override type Container = OrientDBContainer

    override def createContainer(): OrientDBContainer = {
      new OrientDBContainer(
        dockerImageName,
        databaseName,
        serverPassword,
        scriptPath
      )
    }
  }
}
