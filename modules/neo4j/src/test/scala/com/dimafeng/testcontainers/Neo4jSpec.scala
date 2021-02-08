package com.dimafeng.testcontainers

import org.neo4j.driver._

import org.scalatest.flatspec.AnyFlatSpec



class Neo4jSpec extends AnyFlatSpec with ForAllTestContainer {

  override val container: Neo4jContainer = Neo4jContainer()

  "Neo4j container" should "be started" in {

    val driver: Driver = GraphDatabase.driver(container.boltUrl, AuthTokens.basic(container.username, container.password))

    val session = driver.session()

    try {
      val query =  s"MATCH (n) RETURN COUNT(n) as count"
      val resultSet = session.run(query)
      assert( 0 == resultSet.next().get("count").asInt())
    } finally {
      session.close()
    }

    driver.close()
  }
}
