package com.dimafeng.testcontainers

import java.sql.DriverManager

import org.scalatest.FlatSpec

class OracleSpec extends FlatSpec with ForAllTestContainer {

  override val container: OracleContainer = new OracleContainer()

  "Oracle container" should "be started" in {
    Class.forName(container.driverClassName)
    val connection = DriverManager.getConnection(
      container.jdbcUrl,
      container.username,
      container.password
    )

    val prepareStatement = connection.prepareStatement(container.testQueryString)
    try {
      val resultSet = prepareStatement.executeQuery()
      resultSet.next()
      assert(1 == resultSet.getInt(1))
      resultSet.close()
    } finally {
      prepareStatement.close()
    }

    connection.close()
  }
}
