package com.dimafeng.testcontainers

import com.couchbase.client.java.cluster.{BucketSettings, UserSettings}
import org.testcontainers.couchbase.{CouchbaseContainer => JavaCouchbaseContainer}

class CouchbaseContainer(
  dockerImageName: String = CouchbaseContainer.defaultDockerImageName,
  buckets: Seq[CouchbaseContainer.Bucket] = Seq.empty,
  clusterUsername: String = CouchbaseContainer.defaultUsername,
  clusterPassword: String = CouchbaseContainer.defaultPassword,
  memoryQuota: String = CouchbaseContainer.defaultMemoryQuota,
  indexMemoryQuota: String = CouchbaseContainer.defaultIndexMemoryQuota,
  keyValue: Boolean = CouchbaseContainer.defaultKeyValue,
  query: Boolean = CouchbaseContainer.defaultQuery,
  index: Boolean = CouchbaseContainer.defaultIndex,
  primaryIndex: Boolean = CouchbaseContainer.defaultPrimaryIndex,
  fts: Boolean = CouchbaseContainer.defaultFts,
) extends SingleContainer[JavaCouchbaseContainer] {

  import CouchbaseContainer._

  override val container: JavaCouchbaseContainer = {
    val c = new JavaCouchbaseContainer(dockerImageName)

    buckets.foreach {
      case Bucket(bucketSettings, Some(userSettings)) => c.withNewBucket(bucketSettings, userSettings)
      case Bucket(bucketSettings, None) => c.withNewBucket(bucketSettings)
    }

    c.withClusterAdmin(clusterUsername, clusterPassword)
    c.withMemoryQuota(memoryQuota)
    c.withIndexMemoryQuota(indexMemoryQuota)
    c.withKeyValue(keyValue)
    c.withQuery(query)
    c.withIndex(index)
    c.withPrimaryIndex(primaryIndex)
    c.withFts(fts)

    c
  }

  def initCluster(): Unit = container.initCluster()

  def createBucket(bucketSettings: BucketSettings, userSettings: UserSettings, primaryIndex: Boolean): Unit =
    container.createBucket(bucketSettings, userSettings, primaryIndex)

  def callCouchbaseRestAPI(url: String, payload: String): Unit = container.callCouchbaseRestAPI(url, payload)
}

object CouchbaseContainer {

  val defaultDockerImageName = s"${JavaCouchbaseContainer.DOCKER_IMAGE_NAME}${JavaCouchbaseContainer.VERSION}"
  val defaultUsername = "Administrator"
  val defaultPassword = "password"
  val defaultMemoryQuota = "300"
  val defaultIndexMemoryQuota = "300"
  val defaultKeyValue = true
  val defaultQuery = true
  val defaultIndex = true
  val defaultPrimaryIndex = true
  val defaultFts = false

  case class Bucket(bucketSettings: BucketSettings, userSettings: Option[UserSettings])

  case class Def(
    dockerImageName: String = CouchbaseContainer.defaultDockerImageName,
    buckets: Seq[CouchbaseContainer.Bucket] = Seq.empty,
    clusterUsername: String = CouchbaseContainer.defaultUsername,
    clusterPassword: String = CouchbaseContainer.defaultPassword,
    memoryQuota: String = CouchbaseContainer.defaultMemoryQuota,
    indexMemoryQuota: String = CouchbaseContainer.defaultIndexMemoryQuota,
    keyValue: Boolean = CouchbaseContainer.defaultKeyValue,
    query: Boolean = CouchbaseContainer.defaultQuery,
    index: Boolean = CouchbaseContainer.defaultIndex,
    primaryIndex: Boolean = CouchbaseContainer.defaultPrimaryIndex,
    fts: Boolean = CouchbaseContainer.defaultFts,
  ) extends ContainerDef {

    override type Container = CouchbaseContainer

    override def createContainer(): CouchbaseContainer = {
      new CouchbaseContainer(
        dockerImageName,
        buckets,
        clusterUsername,
        clusterPassword,
        memoryQuota,
        indexMemoryQuota,
        keyValue,
        query,
        index,
        primaryIndex,
        fts,
      )
    }
  }
}
