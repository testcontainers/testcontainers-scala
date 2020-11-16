package com.dimafeng.testcontainers.integration

import com.dimafeng.testcontainers.{Container, ForAllTestContainer, MongoDBContainer, MultipleContainers}
import com.dimafeng.testcontainers.implicits._
import org.scalatest.{FlatSpec, Matchers}

class MongoSpec extends FlatSpec with ForAllTestContainer with Matchers {

  private val mongoDefault = MongoDBContainer()
  private val mongo = MongoDBContainer("mongo:4.0.10")

  override val container: Container = MultipleContainers(
    mongoDefault, mongo
  )

  "Default Mongo container" should "be started" in {
    assert(mongoDefault.replicaSetUrl.nonEmpty)
  }

  "Custom image Mongo container" should "be started" in {
    assert(mongo.replicaSetUrl.nonEmpty)
  }

}