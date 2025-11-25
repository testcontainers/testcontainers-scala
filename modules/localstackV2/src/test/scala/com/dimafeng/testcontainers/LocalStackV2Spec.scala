package com.dimafeng.testcontainers

import com.dimafeng.testcontainers.scalatest.TestContainersForAll
import org.scalatest.flatspec.AnyFlatSpec
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.CreateBucketRequest

class LocalStackV2Spec extends AnyFlatSpec with TestContainersForAll {
  override type Containers = LocalStackV2Container

  override def startContainers(): LocalStackV2Container =
    LocalStackV2Container
      .Def(services = Seq("s3"))
      .start()

  "LocalStackV2 container" should "be started" in withContainers {
    localStackContainer =>
      val s3: S3Client = S3Client
        .builder()
        .endpointOverride(localStackContainer.endpoint)
        .credentialsProvider(localStackContainer.staticCredentialsProvider)
        .region(localStackContainer.region)
        .build()

      LocalStackV2Spec.createBucket(s3, "testcontainers-bucket")

      assert(s3.listBuckets().buckets().size() == 1)
  }
}

object LocalStackV2Spec {
  def createBucket(s3: S3Client, bucketName: String): Unit = {
    val request = CreateBucketRequest
      .builder()
      .bucket(bucketName)
      .build()

    s3.createBucket(request)
  }

}
