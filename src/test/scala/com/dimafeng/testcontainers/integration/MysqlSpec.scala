package com.dimafeng.testcontainers.integration

import java.sql.DriverManager

import com.dimafeng.testcontainers.{ForAllTestContainer, MySQLContainer}
import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner

class MysqlSpec extends FlatSpec with ForAllTestContainer {

  override val container = MySQLContainer()

  "Mysql container" should "be started" in {
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
