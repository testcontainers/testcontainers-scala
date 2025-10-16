package com.dimafeng.testcontainers

import com.datastax.driver.core.Cluster
import org.testcontainers.cassandra.{CassandraContainer => JavaCassandraContainer}
import org.testcontainers.utility.DockerImageName

class CassandraContainer(dockerImageNameOverride: Option[DockerImageName] = None,
                         configurationOverride: Option[String] = None,
                         initScript: Option[String] = None,
                         jmxReporting: Boolean = false) extends SingleContainer[JavaCassandraContainer[?]] {

  val cassandraContainer: JavaCassandraContainer[?] = {
    if (dockerImageNameOverride.isEmpty) {
      new JavaCassandraContainer()
    } else {
      new JavaCassandraContainer(dockerImageNameOverride.get)
    }
  }

  if (configurationOverride.isDefined) cassandraContainer.withConfigurationOverride(configurationOverride.get)
  if (initScript.isDefined) cassandraContainer.withInitScript(initScript.get)
  if (jmxReporting) cassandraContainer.withJmxReporting(jmxReporting)

  override val container: JavaCassandraContainer[?] = cassandraContainer

  def cluster: Cluster = cassandraContainer.getCluster

  def username: String = cassandraContainer.getUsername

  def password: String = cassandraContainer.getPassword
}


object CassandraContainer {

  val defaultDockerImageName = "cassandra:3.11.2"

  def apply(dockerImageNameOverride: DockerImageName = null,
            configurationOverride: String = null,
            initScript: String = null,
            jmxReporting: Boolean = false): CassandraContainer = new CassandraContainer(
    Option(dockerImageNameOverride),
    Option(configurationOverride),
    Option(initScript),
    jmxReporting
  )

  case class Def(
    dockerImageName: DockerImageName = DockerImageName.parse(defaultDockerImageName),
    configurationOverride: Option[String] = None,
    initScript: Option[String] = None,
    jmxReporting: Boolean = false
  ) extends ContainerDef {

    override type Container = CassandraContainer

    override def createContainer(): CassandraContainer = {
      new CassandraContainer(
        dockerImageNameOverride = Some(dockerImageName),
        configurationOverride = configurationOverride,
        initScript = initScript,
        jmxReporting = jmxReporting
      )
    }
  }

}
