package com.dimafeng.testcontainers

import org.testcontainers.cassandra.{CassandraContainer => JavaCassandraContainer}
import org.testcontainers.utility.DockerImageName

class CassandraContainer(image: DockerImageName = DockerImageName.parse(CassandraContainer.defaultDockerImageName),
                         configurationOverride: Option[String] = None,
                         initScript: Option[String] = None) extends SingleContainer[JavaCassandraContainer] {

  val cassandraContainer: JavaCassandraContainer = new JavaCassandraContainer(image)

  if (configurationOverride.isDefined) cassandraContainer.withConfigurationOverride(configurationOverride.get)
  if (initScript.isDefined) cassandraContainer.withInitScript(initScript.get)

  override val container: JavaCassandraContainer = cassandraContainer

  def username: String = cassandraContainer.getUsername

  def password: String = cassandraContainer.getPassword
}


object CassandraContainer {

  val defaultDockerImageName = "cassandra:3.11.2"

  def apply(image: DockerImageName = DockerImageName.parse(defaultDockerImageName),
            configurationOverride: String = null,
            initScript: String = null): CassandraContainer = new CassandraContainer(
    image,
    Option(configurationOverride),
    Option(initScript)
  )

  case class Def(
    dockerImageName: DockerImageName = DockerImageName.parse(defaultDockerImageName),
    configurationOverride: Option[String] = None,
    initScript: Option[String] = None
  ) extends ContainerDef {

    override type Container = CassandraContainer

    override def createContainer(): CassandraContainer = {
      new CassandraContainer(
        image = dockerImageName,
        configurationOverride = configurationOverride,
        initScript = initScript
      )
    }
  }

}
