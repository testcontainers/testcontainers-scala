package com.dimafeng.testcontainers

import java.sql.DriverManager
import org.scalatest.flatspec.AnyFlatSpec
import org.testcontainers.utility.DockerImageName

class MysqlSpec extends AnyFlatSpec with ForAllTestContainer {

  override val container: MySQLContainer = MySQLContainer(mysqlImageVersion = DockerImageName.parse("mysql:5.7.34"))

  "Mysql container" should "be started" in {
    Class.forName(container.driverClassName)
    val connection = DriverManager.getConnection(container.jdbcUrl + "&useSSL=false", container.username, container.password)

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
