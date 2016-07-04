package com.dimafeng.testcontainers

import org.testcontainers.containers.{MySQLContainer => OTCMySQLContainer}

class MySQLContainer(configurationOverride: Option[String] = None) extends SingleContainer[OTCMySQLContainer[_]] {

  private val c = new OTCMySQLContainer()
  configurationOverride.foreach { v => c.withConfigurationOverride(v); Unit }

  override def container = c

  def driverClassName = c.getDriverClassName

  def jdbcUrl = c.getJdbcUrl

  def password = c.getPassword

  def testQueryString = c.getTestQueryString

  def username = c.getUsername
}

object MySQLContainer {
  def apply(configurationOverride: String = null) = new MySQLContainer(Option(configurationOverride))
}