package com.dimafeng.testcontainers

import com.dimafeng.testcontainers.scalatest.TestContainersForAll
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import sttp.client3.{HttpURLConnectionBackend, UriContext, basicRequest}
import sttp.model.StatusCode

class OpensearchSpec
    extends AnyFlatSpec
    with TestContainersForAll
    with Matchers {
  override type Containers = OpensearchContainer
  override def startContainers(): Containers =
    OpensearchContainer.Def().start()

  "Opensearch container" should "be started" in withContainers { container =>
    val backend = HttpURLConnectionBackend()

    basicRequest
      .get(uri"${container.httpHost}")
      .send(backend)
      .code should be(StatusCode.Ok)
  }
}
