package com.dimafeng.testcontainers

import org.neo4j.driver._

import org.scalatest.FlatSpec



class Neo4jSpec extends FlatSpec with ForAllTestContainer {

  override val container = Neo4jContainer()

  "Neo4j container" should "be started" in {

    val driver = GraphDatabase.driver(container.boltUrl, AuthTokens.basic(container.username, container.password))

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
