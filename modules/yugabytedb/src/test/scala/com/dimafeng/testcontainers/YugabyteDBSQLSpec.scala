package com.dimafeng.testcontainers

import com.dimafeng.testcontainers.scalatest.TestContainersForAll
import org.scalatest.flatspec.AnyFlatSpec

import java.sql.DriverManager

class YugabyteDBSQLSpec extends AnyFlatSpec with TestContainersForAll {
  override type Containers = YugabyteDBYSQLContainer

  val databaseName = "test_db"

  override def startContainers(): YugabyteDBYSQLContainer =
    YugabyteDBYSQLContainer
      .Def()
      .withDatabaseName(databaseName)
      .withUsername("yugabyte")
      .withPassword("yugabyte")
      .start()

  "Yugabytedb container" should "be started" in withContainers { yugabytedb =>
    Class.forName(yugabytedb.driverClassName)
    val connection = DriverManager.getConnection(yugabytedb.jdbcUrl, yugabytedb.username, yugabytedb.password)

    val preparedStatement = connection.prepareStatement(yugabytedb.testQueryString)
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
