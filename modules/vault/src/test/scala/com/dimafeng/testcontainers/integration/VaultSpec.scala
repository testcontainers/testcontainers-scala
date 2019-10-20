package com.dimafeng.testcontainers.integration

import com.dimafeng.testcontainers.{ForAllTestContainer, VaultContainer}
import io.restassured.RestAssured.given
import io.restassured.module.scala.RestAssuredSupport.AddThenToResponse
import org.scalatest.{FlatSpec, Matchers}

class VaultSpec extends FlatSpec with ForAllTestContainer with Matchers {

  private val port = 8200
  override val container = VaultContainer(vaultPort = Some(port))

  "Vault container" should "be started" in {
    given().
      baseUri(s"http://${container.containerIpAddress}:${container.mappedPort(port)}").
      when().
      get("/v1/sys/health").
      Then().
      statusCode(200)
  }

}
