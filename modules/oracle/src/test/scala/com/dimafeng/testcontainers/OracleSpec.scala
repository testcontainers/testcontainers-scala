package com.dimafeng.testcontainers

import java.sql.DriverManager

import com.dimafeng.testcontainers.ForAllTestContainer
import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner

class OracleSpec extends FlatSpec with ForAllTestContainer {

  override val container: OracleContainer = OracleContainer(OracleContainer.defaultDockerImageName)

  "Oracle container" should "be started" in {
    Class.forName(container.driverClassName)
    val connection = DriverManager.getConnection(container.jdbcUrl, container.username, container.password)

    val prepareStatement = connection.prepareStatement("select 1")
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
