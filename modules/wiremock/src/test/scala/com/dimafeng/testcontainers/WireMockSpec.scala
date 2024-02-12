package com.dimafeng.testcontainers

import com.dimafeng.testcontainers.scalatest.TestContainersForAll
import org.scalatest.flatspec.AnyFlatSpec
import sttp.client3._

class WireMockSpec extends AnyFlatSpec with TestContainersForAll {
  override type Containers = WireMockContainer

  override def startContainers(): WireMockContainer = {
    WireMockContainer
      .Def()
      .withMappingFromResource("foo_mapping.json")
      .withMappingFromResource("bar_mapping.json")
      .withFileFromResource("bar_body.json")
      .start()
  }

  "WireMock container" should "start" in withContainers { container =>
    val backend = HttpURLConnectionBackend()

    val response = basicRequest
      .get(uri"${container.getUrl("/__admin/health")}")
      .send(backend)

    assert(response.code.code == 200)
  }

  "WireMock container" should "support adding mappings and files" in withContainers {
    container =>
      val backend = HttpURLConnectionBackend()

      val fooResponse =
        basicRequest.get(uri"${container.getUrl("/foo")}").send(backend)
      val barResponse =
        basicRequest.get(uri"${container.getUrl("/bar")}").send(backend)

      assert(fooResponse.body == Right("""{"result":"hello"}"""))
      assert(barResponse.body == Right("""{"result":"world"}\n""".replaceAllLiterally("\\n", "\n")))
  }
}
