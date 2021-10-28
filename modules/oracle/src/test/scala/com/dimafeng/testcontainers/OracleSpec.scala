package com.dimafeng.testcontainers

import java.sql.DriverManager

import org.scalatest.flatspec.AnyFlatSpec

class OracleSpec extends AnyFlatSpec with ForAllTestContainer {

  override val container: OracleContainer = OracleContainer("gvenzl/oracle-xe:18.4.0-slim")

  "Oracle container" should "be started" in {
    System.setProperty("oracle.jdbc.timezoneAsRegion", "false")
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
