package com.dimafeng.testcontainers

import org.testcontainers.containers.{MySQLContainer => OTCMySQLContainer}

class MySQLContainer(configurationOverride: Option[String] = None) extends SingleContainer[OTCMySQLContainer[_]] {

  type OTCContainer = OTCMySQLContainer[T] forSome {type T <: OTCMySQLContainer[T]}
  override val container: OTCContainer = new OTCMySQLContainer()
  configurationOverride.foreach(container.withConfigurationOverride)

  def driverClassName: String = container.getDriverClassName

  def jdbcUrl: String = container.getJdbcUrl

  def password: String = container.getPassword

  def testQueryString: String = container.getTestQueryString

  def username: String = container.getUsername
}

object MySQLContainer {
  def apply(configurationOverride: String = null): MySQLContainer = new MySQLContainer(Option(configurationOverride))
}