package com.dimafeng.testcontainers

import com.orientechnologies.orient.core.db.{ODatabaseSession, OrientDB}
import org.testcontainers.orientdb.{OrientDBContainer => JavaOrientDBContainer}
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

  def serverUrl: String = container.getServerUrl

  def dbUrl: String = container.getDbUrl
  
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
