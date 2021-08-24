package com.dimafeng.testcontainers

import com.dimafeng.testcontainers.RabbitMQContainer.{Exchange, Permission, User, VHost}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.testcontainers.utility.DockerImageName
import sttp.client3.{HttpURLConnectionBackend, UriContext, basicRequest}

class RabbitMQSpec extends AnyFlatSpec with ForAllTestContainer with Matchers {
  import RabbitMQSpec._

  override val container: Container = MultipleContainers(
    defaultRabbitContainer, customRabbitContainer
  )

  "Default Rabbit container" should "start" in {
    val baseUri = defaultRabbitContainer.httpUrl
    val request =
      basicRequest
        .auth.basic(testUsername, testPassword)
        .get(uri"$baseUri/")

    val eitherContainerIsOnline =
      request.send(httpClientBackend).body.map(_ => true)

    assertResult(Right(true))(eitherContainerIsOnline)
  }


  "Custom Rabbit container" should "start and load exchanges config" in {
    val baseUri = customRabbitContainer.httpUrl
    val request =
      basicRequest
        .auth.basic(testUsername, testPassword)
        .get(uri"$baseUri/api/exchanges")

    val eitherExchangeWasLoaded =
      request.send(httpClientBackend).body.map(_.contains(testExchange))

    assertResult(Right(true))(eitherExchangeWasLoaded)
  }

  "Custom Rabbit container" should "start and load users config" in {
    val baseUri = customRabbitContainer.httpUrl
    val request =
      basicRequest
        .auth.basic(testUsername, testPassword)
        .get(uri"$baseUri/api/users")

    val eitheruserWasLoaded =
      request.send(httpClientBackend).body.map(_.contains(testUsername))

    assertResult(Right(true))(eitheruserWasLoaded)
  }
}

object RabbitMQSpec {
  val testExchange = "test-exchange"
  val testUsername = "test-user"
  val testPassword = "test-password"
  val httpClientBackend = HttpURLConnectionBackend()

  val defaultRabbitContainer: RabbitMQContainer = RabbitMQContainer()
  val customRabbitContainer: RabbitMQContainer = RabbitMQContainer(
    dockerImageName = DockerImageName.parse(RabbitMQContainer.defaultDockerImageName),
    adminPassword = RabbitMQContainer.defaultAdminPassword,
    queues = Seq.empty,
    exchanges = Seq(
      Exchange(
        name = testExchange,
        exchangeType = "direct",
        arguments = Map.empty,
        vhost = Some("test-vhost")
      )
    ),
    bindings = Seq.empty,
    users = Seq(
      User(
        name = testUsername,
        password = testPassword,
        tags = Set("administrator")
      )
    ),
    vhosts = Seq(VHost(name = "test-vhost")),
    vhostsLimits = Seq.empty,
    operatorPolicies = Seq.empty,
    policies = Seq.empty,
    parameters = Seq.empty,
    permissions = Seq(
      Permission(
        vhost = "test-vhost",
        user = testUsername,
        configure = ".*",
        write = ".*",
        read = ".*"
      )
    )
  )
}