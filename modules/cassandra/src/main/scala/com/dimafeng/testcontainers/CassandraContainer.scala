package com.dimafeng.testcontainers

import org.testcontainers.containers.{CassandraContainer => OTCCassandraContainer, GenericContainer => OTCGenericContainer}

class CassandraContainer(dockerImageNameOverride: Option[String] = None,
                         configurationOverride: Option[String] = None,
                         initScript: Option[String] = None,
                         jmxReporting: Boolean = false) extends SingleContainer[OTCCassandraContainer[_]] {

  val cassandraContainer: OTCCassandraContainer[_] = {
    if (dockerImageNameOverride.isEmpty) {
      new OTCCassandraContainer()
    } else {
      new OTCCassandraContainer(dockerImageNameOverride.get)
    }
  }

  if (configurationOverride.isDefined) cassandraContainer.withConfigurationOverride(configurationOverride.get)
  if (initScript.isDefined) cassandraContainer.withInitScript(initScript.get)
  if (jmxReporting) cassandraContainer.withJmxReporting(jmxReporting)

  override val container: OTCCassandraContainer[_] = cassandraContainer
}


object CassandraContainer {

  def apply(dockerImageNameOverride: String = null,
            configurationOverride: String = null,
            initScript: String = null,
            jmxReporting: Boolean = false): CassandraContainer = new CassandraContainer(
    Option(dockerImageNameOverride),
    Option(configurationOverride),
    Option(initScript),
    jmxReporting
  )

}
