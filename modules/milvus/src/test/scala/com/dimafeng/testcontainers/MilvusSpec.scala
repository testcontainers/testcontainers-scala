package com.dimafeng.testcontainers

import com.dimafeng.testcontainers.scalatest.TestContainersForAll
import org.scalatest.flatspec.AnyFlatSpec
import io.milvus.client.{MilvusServiceClient, MilvusClient}
import io.milvus.param.ConnectParam
import io.milvus.param.collection.CreateDatabaseParam

import java.util

class MilvusSpec extends AnyFlatSpec with TestContainersForAll {
  override type Containers = MilvusContainer

  override def startContainers(): MilvusContainer =
    MilvusContainer.Def().start()

  "Milvus container" should "be started" in withContainers {
    milvusContainer =>
      val connect = ConnectParam
        .newBuilder()
        .withUri(milvusContainer.endpoint)
        .build()
      val milvus = new MilvusServiceClient(connect)
      val dbName = "testcontainers_database"

      MilvusSpec.createDatabase(milvus, dbName)

      assert(milvus.listDatabases().getData.getDbNamesList.containsAll(util.Arrays.asList("default", dbName)))
  }
}

object MilvusSpec {
  def createDatabase(milvusClient: MilvusClient, databaseName: String): Unit = {
    val param = CreateDatabaseParam.newBuilder().withDatabaseName(databaseName).build()

    milvusClient.createDatabase(param).getData
  }

}
