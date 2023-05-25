package com.dimafeng.testcontainers.integration

import com.dimafeng.testcontainers.{ForAllTestContainer, VaultContainer}
import io.restassured.RestAssured.given
import io.restassured.module.scala.RestAssuredSupport.AddThenToResponse
import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec
import org.testcontainers.utility.DockerImageName

class VaultSpec extends AnyFlatSpec with ForAllTestContainer with Matchers {

  private val port = 8200
  override val container: VaultContainer = VaultContainer(DockerImageName.parse("vault:1.1.3"), vaultPort = Some(port))

  "Vault container" should "be started" in {
    given().
      baseUri(s"http://${container.containerIpAddress}:${container.mappedPort(port)}").
      when().
      get("/v1/sys/health").
      Then().
      statusCode(200)
  }

}
