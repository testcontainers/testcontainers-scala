package com.dimafeng.testcontainers

import com.dimafeng.testcontainers.scalatest.TestContainersForAll
import org.mockserver.client.MockServerClient
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import sttp.client3.{HttpURLConnectionBackend, UriContext, basicRequest}

class MockServerSpec
    extends AnyFlatSpec
    with TestContainersForAll
    with Matchers {
  override type Containers = MockServerContainer

  override def startContainers(): MockServerContainer =
    MockServerContainer.Def().start()

  "MockServer container" should "be started" in withContainers {
    mockServerContainer =>
      val backend = HttpURLConnectionBackend()

      val mockServerClient = new MockServerClient(
        mockServerContainer.serverHost,
        mockServerContainer.serverPort
      )

      mockServerClient
        .when(request("/hello"))
        .respond(response("world"))

      basicRequest
        .get(uri"${mockServerContainer.endpoint}/hello")
        .send(backend)
        .body should be(Right("world"))
  }
}
