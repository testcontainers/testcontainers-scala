package com.dimafeng.testcontainers

import com.dimafeng.testcontainers.RabbitMQContainer.{Exchange, Permission, User, VHost}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.testcontainers.utility.DockerImageName
import sttp.client3.{HttpURLConnectionBackend, UriContext, basicRequest}

import scala.util.Either

class RabbitMQSpec extends AnyFlatSpec with ForAllTestContainer with Matchers {
  import RabbitMQSpec._

  override val container: Container = MultipleContainers(
    defaultRabbitContainer,
    customRabbitContainer,
    customRabbitContainerViaDef
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


  "Custom Rabbit container" should "start and load exchanges config" in {
    val baseUri = customRabbitContainer.httpUrl
    val request =
      basicRequest
        .auth.basic(testUsername, testPassword)
        .get(uri"$baseUri/api/exchanges")

    val eitherExchangeWasLoaded =
      request.send(httpClientBackend).body match {
        case Right(v) => Right(v.contains(testExchange))
        case e@Left(_) => e
      }

    assertResult(Right(true))(eitherExchangeWasLoaded)
  }

  "Custom Rabbit container" should "start and load users config" in {
    val baseUri = customRabbitContainer.httpUrl
    val request =
      basicRequest
        .auth.basic(testUsername, testPassword)
        .get(uri"$baseUri/api/users")

    val eitherUserWasLoaded =
      request.send(httpClientBackend).body match {
        case Right(v) => Right(v.contains(testUsername))
        case e@Left(_) => e
      }

    assertResult(Right(true))(eitherUserWasLoaded)
  }

  "Custom Rabbit container" should "start and load users config (Via .Def)" in {
    val baseUri = customRabbitContainerViaDef.httpUrl
    val request =
      basicRequest
        .auth.basic(testUsername, testPassword)
        .get(uri"$baseUri/api/users")

    val eitherUserWasLoaded =
      request.send(httpClientBackend).body match {
        case Right(v) => Right(v.contains(testUsername))
        case e@Left(_) => e
      }

    assertResult(Right(true))(eitherUserWasLoaded)
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
  val customRabbitContainerViaDef = RabbitMQContainer.Def(
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
  ).createContainer()
}
