package com.dimafeng.testcontainers

import java.sql.Driver

import org.testcontainers.containers.{JdbcDatabaseContainer => JavaJdbcDatabaseContainer}

import scala.concurrent.duration._

trait JdbcDatabaseContainer { self: SingleContainer[_ <: JavaJdbcDatabaseContainer[_]] =>

  def driverClassName: String = underlyingUnsafeContainer.getDriverClassName

  def jdbcUrl: String = underlyingUnsafeContainer.getJdbcUrl

  def databaseName: String = underlyingUnsafeContainer.getDatabaseName

  def username: String = underlyingUnsafeContainer.getUsername

  def password: String = underlyingUnsafeContainer.getPassword

  def jdbcDriverInstance: Driver = underlyingUnsafeContainer.getJdbcDriverInstance
}

object JdbcDatabaseContainer {
  case class CommonParams(
    startupTimeout: FiniteDuration = 120.seconds,
    connectTimeout: FiniteDuration = 120.seconds,
    initScriptPath: Option[String] = None
  ) {
    private[testcontainers] def applyTo[C <: JavaJdbcDatabaseContainer[_]](container: C): Unit = {
      container.withStartupTimeoutSeconds(startupTimeout.toSeconds.toInt)
      container.withConnectTimeoutSeconds(connectTimeout.toSeconds.toInt)
      initScriptPath.foreach(container.withInitScript)
    }
  }
}
