package com.dimafeng.testcontainers

import org.testcontainers.containers.{CassandraContainer => JavaCassandraContainer}

class CassandraContainer(dockerImageNameOverride: Option[String] = None,
                         configurationOverride: Option[String] = None,
                         initScript: Option[String] = None,
                         jmxReporting: Boolean = false) extends SingleContainer[JavaCassandraContainer[_]] {

  val cassandraContainer: JavaCassandraContainer[_] = {
    if (dockerImageNameOverride.isEmpty) {
      new JavaCassandraContainer()
    } else {
      new JavaCassandraContainer(dockerImageNameOverride.get)
    }
  }

  if (configurationOverride.isDefined) cassandraContainer.withConfigurationOverride(configurationOverride.get)
  if (initScript.isDefined) cassandraContainer.withInitScript(initScript.get)
  if (jmxReporting) cassandraContainer.withJmxReporting(jmxReporting)

  override val container: JavaCassandraContainer[_] = cassandraContainer
}


object CassandraContainer {

  val defaultDockerImageName = "cassandra:3.11.2"

  def apply(dockerImageNameOverride: String = null,
            configurationOverride: String = null,
            initScript: String = null,
            jmxReporting: Boolean = false): CassandraContainer = new CassandraContainer(
    Option(dockerImageNameOverride),
    Option(configurationOverride),
    Option(initScript),
    jmxReporting
  )

  case class Def(
    dockerImageName: String = defaultDockerImageName,
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
