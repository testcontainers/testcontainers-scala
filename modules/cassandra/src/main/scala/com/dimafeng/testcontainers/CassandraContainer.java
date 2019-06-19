package com.dimafeng.testcontainers;

class CassandraContainer(dockerImageNameOverride: Option[String] = None,
                         configurationOverride: Option[String] = None,
                         initScript: Option[String] = None,
                         jmxReporting: Boolean = false) extends SingleContainer[OTCCassandraContainer[_]] {

  type OTCContainer = OTCGenericContainer[T] forSome {type T <: OTCCassandraContainer[T]}

  val cassandraContainer: OTCCassandraContainer[Nothing] = {
    if (dockerImageNameOverride.isEmpty) {
      new OTCCassandraContainer()
    } else {
      new OTCCassandraContainer(dockerImageNameOverride.get)
    }
  }

  if (configurationOverride.isDefined) cassandraContainer.withConfigurationOverride(configurationOverride.get)
  if (initScript.isDefined) cassandraContainer.withConfigurationOverride(initScript.get)
  if (jmxReporting) cassandraContainer.withJmxReporting(jmxReporting)

  override val container: OTCCassandraContainer[_] = cassandraContainer
}
