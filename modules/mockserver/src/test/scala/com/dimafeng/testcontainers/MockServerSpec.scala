package com.dimafeng.testcontainers

import com.dimafeng.testcontainers.scalatest.TestContainersForAll
import org.mockserver.client.MockServerClient
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatest.matchers.should.Matchers.not.be
import sttp.client3.{UriContext, basicRequest}

class MockServerSpec extends AnyFlatSpec with TestContainersForAll {
  override type Containers = MockServerContainer

  override def startContainers(): MockServerContainer =
    MockServerContainer.Def().start()

  "MockServer container" should "be started" in withContainers {
    mockServerContainer =>
      val mockServerClient = new MockServerClient(
        mockServerContainer.serverHost,
        mockServerContainer.serverPort
      )

      mockServerClient
        .when(
          request()
            .withMethod("GET")
            .withPath("/hello")
        )
        .respond(
          response.withBody("world")
        )

      basicRequest
        .get(uri"${mockServerContainer.endpoint}/hello")
        .body should be(Right("world"))
  }
}
