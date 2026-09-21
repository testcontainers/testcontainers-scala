package com.dimafeng.testcontainers

import com.dimafeng.testcontainers.scalatest.TestContainerForAll
import org.scalatest.flatspec.AnyFlatSpec

class S3MockSpec extends AnyFlatSpec with TestContainerForAll {

  override val containerDef = S3MockContainer.Def()

  "S3Mock container" should "be started and expose its endpoints" in withContainers { s3Mock =>
    assert(s3Mock.httpEndpoint.startsWith("http://"))
    assert(s3Mock.httpsEndpoint.startsWith("https://"))
  }
}