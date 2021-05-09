package com.dimafeng.testcontainers

import java.sql.DriverManager
import org.scalatest.flatspec.AnyFlatSpec
import org.testcontainers.utility.DockerImageName

import java.util.Properties

class MysqlSpec extends AnyFlatSpec with ForAllTestContainer {

  override val container: MySQLContainer = MySQLContainer(mysqlImageVersion = DockerImageName.parse("mysql:5.7.34"))

  "Mysql container" should "be started" in {
    Class.forName(container.driverClassName)
    val properties = new Properties();
    properties.setProperty("user", container.username);
    properties.setProperty("password", container.password)
    properties.setProperty("useSSL", "false")
    val connection = DriverManager.getConnection(container.jdbcUrl, properties)

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
