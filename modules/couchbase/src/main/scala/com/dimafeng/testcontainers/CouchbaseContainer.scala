package com.dimafeng.testcontainers

import java.util.EnumSet

import org.testcontainers.couchbase.{CouchbaseService, BucketDefinition => JavaBucketDefinition, CouchbaseContainer => JavaCouchbaseContainer}
import org.testcontainers.utility.DockerImageName

import scala.collection.JavaConverters._

case class CouchbaseContainer(
  dockerImageName: DockerImageName = DockerImageName.parse(CouchbaseContainer.defaultDockerImageName),
  buckets: Seq[CouchbaseContainer.BucketDefinition] = Seq.empty,
  username: String = CouchbaseContainer.defaultUsername,
  password: String = CouchbaseContainer.defaultPassword,
  enabledServices: Set[CouchbaseService] = CouchbaseContainer.defaultEnabledServices
) extends SingleContainer[JavaCouchbaseContainer] {

  import CouchbaseContainer._

  override val container: JavaCouchbaseContainer = {
    val c = new JavaCouchbaseContainer(dockerImageName)

    buckets.foreach { (bucket: BucketDefinition) =>
      val javaBucket = new JavaBucketDefinition(bucket.name)
        .withQuota(bucket.quota)
        .withPrimaryIndex(bucket.hasPrimaryIndex)
      c.withBucket(javaBucket)
    }

    c.withCredentials(username, password)
    c.withEnabledServices(enabledServices.toSeq: _*)

    c
  }


  def bootstrapCarrierDirectPort: Int = container.getBootstrapCarrierDirectPort

  def bootstrapHttpDirectPort: Int = container.getBootstrapHttpDirectPort

  def connectionString: String = container.getConnectionString
}

object CouchbaseContainer {

  val defaultImage = "couchbase/server"
  val defaultTag = "6.5.1"
  val defaultDockerImageName = s"$defaultImage:$defaultTag"
  val defaultUsername = "Administrator"
  val defaultPassword = "password"
  val defaultEnabledServices: Set[CouchbaseService] = EnumSet.allOf(classOf[CouchbaseService]).asScala.toSet

  val defaultQuota = 100
  val defaultHasPrimaryIndex = true

  case class BucketDefinition(name: String, quota: Int = defaultQuota, hasPrimaryIndex: Boolean = defaultHasPrimaryIndex)

  case class Def(
    dockerImageName: DockerImageName = DockerImageName.parse(CouchbaseContainer.defaultDockerImageName),
    buckets: Seq[CouchbaseContainer.BucketDefinition] = Seq.empty,
    username: String = CouchbaseContainer.defaultUsername,
    password: String = CouchbaseContainer.defaultPassword,
    enabledServices: Set[CouchbaseService] = CouchbaseContainer.defaultEnabledServices
  ) extends ContainerDef {

    override type Container = CouchbaseContainer

    override def createContainer(): CouchbaseContainer = {
      new CouchbaseContainer(
        dockerImageName,
        buckets,
        username,
        password,
        enabledServices
      )
    }
  }
}
