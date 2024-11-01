package com.dimafeng.testcontainers

import com.dimafeng.testcontainers.scalatest.TestContainersForAll
import io.qdrant.client.{QdrantClient, QdrantGrpcClient}
import org.scalatest.flatspec.AnyFlatSpec

import java.util.UUID

class QdrantSpec extends AnyFlatSpec with TestContainersForAll {
  override type Containers = QdrantContainer

  override def startContainers(): QdrantContainer =
    QdrantContainer.Def().withApiKey(QdrantSpec.apiKey).start()

  "Qdrant container" should "be started" in withContainers { qdrantContainer =>
    val client = new QdrantClient(
      QdrantGrpcClient
        .newBuilder(qdrantContainer.host, qdrantContainer.grpcPort, false)
        .withApiKey(QdrantSpec.apiKey)
        .build()
    )
    val healthCheckReply = client.healthCheckAsync().get()

    assert(healthCheckReply.getVersion.nonEmpty)
  }

}

object QdrantSpec {
  val apiKey: String = UUID.randomUUID().toString
}
