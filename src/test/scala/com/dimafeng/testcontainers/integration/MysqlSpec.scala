package com.dimafeng.testcontainers.integration

import java.sql.DriverManager

import com.dimafeng.testcontainers.{Container, ForAllTestContainer}
import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner
import org.testcontainers.containers.MySQLContainer

@RunWith(classOf[JUnitRunner])
class MysqlSpec extends FlatSpec with ForAllTestContainer {

  val mysqlContainer = new MySQLContainer()
  override val container: Container = Container(mysqlContainer)

  "Mysql container" should "be started" in {
    Class.forName(mysqlContainer.getDriverClassName)
    val connection = DriverManager.getConnection(mysqlContainer.getJdbcUrl, mysqlContainer.getUsername, mysqlContainer.getPassword)

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
