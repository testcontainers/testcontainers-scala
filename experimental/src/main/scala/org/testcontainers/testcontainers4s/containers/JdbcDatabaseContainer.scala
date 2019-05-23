package org.testcontainers.testcontainers4s.containers

import java.sql.Driver

import org.testcontainers.containers.{JdbcDatabaseContainer => JavaJdbcDatabaseContainer}

trait JdbcDatabaseContainer[T <: JavaJdbcDatabaseContainer[_]] { container: Container.Aux[T] =>

  def driverClassName: String = underlyingUnsafeContainer.getDriverClassName

  def jdbcDriverInstance: Driver = underlyingUnsafeContainer.getJdbcDriverInstance

  def jdbcUrl: String = underlyingUnsafeContainer.getJdbcUrl

  def databaseName: String = underlyingUnsafeContainer.getDatabaseName

  def username: String = underlyingUnsafeContainer.getUsername

  def password: String = underlyingUnsafeContainer.getPassword
}
