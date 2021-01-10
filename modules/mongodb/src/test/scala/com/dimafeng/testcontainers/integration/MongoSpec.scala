package com.dimafeng.testcontainers.integration

import com.dimafeng.testcontainers.{Container, ForAllTestContainer, MongoDBContainer, MultipleContainers}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class MongoSpec extends AnyFlatSpec with ForAllTestContainer with Matchers {

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