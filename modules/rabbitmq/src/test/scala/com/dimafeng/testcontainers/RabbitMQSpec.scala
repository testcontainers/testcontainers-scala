package com.dimafeng.testcontainers

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import sttp.client3.{HttpURLConnectionBackend, UriContext, basicRequest}

class RabbitMQSpec extends AnyFlatSpec with ForAllTestContainer with Matchers {
  import RabbitMQSpec._

  override val container: Container = MultipleContainers(
    defaultRabbitContainer,
  )

  "Default Rabbit container" should "start" in {
    val baseUri = defaultRabbitContainer.httpUrl
    val request =
      basicRequest
        .auth.basic(testUsername, testPassword)
        .get(uri"$baseUri/")

    val eitherContainerIsOnline =
      request.send(httpClientBackend).body match {
        case Right(_) => Right(true)
        case e@Left(_) => e
      }

    assertResult(Right(true))(eitherContainerIsOnline)
  }


}

object RabbitMQSpec {
  private val testExchange = "test-exchange"
  private val testUsername = "test-user"
  private val testPassword = "test-password"
  private val httpClientBackend = HttpURLConnectionBackend()

  private val defaultRabbitContainer: RabbitMQContainer = RabbitMQContainer()
}
