package com.dimafeng.testcontainers

import org.testcontainers.containers.{MySQLContainer => OTCMySQLContainer}

class MySQLContainer(configurationOverride: Option[String] = None) extends SingleContainer[OTCMySQLContainer[_]] {

  override  val container = new OTCMySQLContainer()
  configurationOverride.foreach { v => container.withConfigurationOverride(v); Unit }

  def driverClassName = container.getDriverClassName

  def jdbcUrl = container.getJdbcUrl

  def password = container.getPassword

  def testQueryString = container.getTestQueryString

  def username = container.getUsername
}

object MySQLContainer {
  def apply(configurationOverride: String = null) = new MySQLContainer(Option(configurationOverride))
}