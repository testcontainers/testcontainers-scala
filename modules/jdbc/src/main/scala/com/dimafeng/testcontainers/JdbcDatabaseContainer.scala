package com.dimafeng.testcontainers

import java.sql.Driver

import org.testcontainers.containers.{JdbcDatabaseContainer => JavaJdbcDatabaseContainer}

trait JdbcDatabaseContainer { self: SingleContainer[_ <: JavaJdbcDatabaseContainer[_]] =>

  def driverClassName: String = underlyingUnsafeContainer.getDriverClassName

  def jdbcUrl: String = underlyingUnsafeContainer.getJdbcUrl

  def databaseName: String = underlyingUnsafeContainer.getDatabaseName

  def username: String = underlyingUnsafeContainer.getUsername

  def password: String = underlyingUnsafeContainer.getPassword

  def jdbcDriverInstance: Driver = underlyingUnsafeContainer.getJdbcDriverInstance
}
