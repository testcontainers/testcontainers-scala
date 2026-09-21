package com.dimafeng.testcontainers

import com.adobe.testing.s3mock.testcontainers.{S3MockContainer => JavaS3MockContainer}
import org.testcontainers.utility.DockerImageName

case class S3MockContainer(
  dockerImageName: DockerImageName = DockerImageName.parse(S3MockContainer.defaultDockerImageName)
) extends SingleContainer[JavaS3MockContainer] {

  override val container: JavaS3MockContainer = new JavaS3MockContainer(dockerImageName)

  /** S3-compatible HTTP endpoint, e.g. `http://localhost:32835`. */
  def httpEndpoint: String = container.getHttpEndpoint

  /** S3-compatible HTTPS endpoint (self-signed certificate). */
  def httpsEndpoint: String = container.getHttpsEndpoint

  /** Retain the file system contents when the container exits (default: discard). */
  def withRetainFilesOnExit(retainFilesOnExit: Boolean): this.type = {
    container.withRetainFilesOnExit(retainFilesOnExit)
    this
  }

  /** Comma-separated list of KMS key refs S3Mock should treat as valid. */
  def withValidKmsKeys(kmsKeys: String): this.type = {
    container.withValidKmsKeys(kmsKeys)
    this
  }

  /** Comma-separated list of buckets to create on startup. */
  def withInitialBuckets(initialBuckets: String): this.type = {
    container.withInitialBuckets(initialBuckets)
    this
  }

  /** Mount a host directory as S3Mock's root. Docker must be able to read/write into it. */
  def withVolumeAsRoot(root: String): this.type = {
    container.withVolumeAsRoot(root)
    this
  }
}

object S3MockContainer {

  val defaultImage           = "adobe/s3mock"
  val defaultTag             = "4.11.0"
  val defaultDockerImageName = s"$defaultImage:$defaultTag"

  case class Def(
    dockerImageName: DockerImageName = DockerImageName.parse(S3MockContainer.defaultDockerImageName)
  ) extends ContainerDef {

    override type Container = S3MockContainer

    override def createContainer(): S3MockContainer =
      new S3MockContainer(dockerImageName)
  }
}
