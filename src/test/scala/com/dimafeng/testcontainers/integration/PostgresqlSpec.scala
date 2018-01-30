package com.dimafeng.testcontainers.integration

import java.sql.DriverManager

import com.dimafeng.testcontainers.{ForAllTestContainer, PostgreSQLContainer}
import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner

class PostgresqlSpec extends FlatSpec with ForAllTestContainer  {

  override val container = PostgreSQLContainer()

  "PostgreSQL container" should "be started" in {
    Class.forName(container.driverClassName)
    val connection = DriverManager.getConnection(container.jdbcUrl, container.username, container.password)

    val preparedStatement = connection.prepareStatement(container.testQueryString)
    try {
      val resultSet = preparedStatement.executeQuery()
      resultSet.next()
      assert(1 == resultSet.getInt(1))
      resultSet.close()
    } finally {
      preparedStatement.close()
      connection.close()
    }
  }
}
