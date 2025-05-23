package com.dimafeng.testcontainers

import com.dimafeng.testcontainers.scalatest.TestContainersForAll
import org.scalatest.flatspec.AnyFlatSpec
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient

import java.net.URI

class DynamoSpec extends AnyFlatSpec with TestContainersForAll {
  override type Containers = DynamoContainer

  override def startContainers(): DynamoContainer =
    DynamoContainer.Def().start()

  "DynamoDB container" should "be started" in withContainers { container =>
    val credentialsProvider = StaticCredentialsProvider.create(AwsBasicCredentials.create("dummy", "dummy"))
    val client = DynamoDbClient
      .builder()
      .endpointOverride(new URI(container.getEndpointUrl))
      .region(Region.EU_CENTRAL_1)
      .credentialsProvider(credentialsProvider)
      .build()
    val tables = client.listTables().tableNames()

    assert(tables.isEmpty())
  }
}
