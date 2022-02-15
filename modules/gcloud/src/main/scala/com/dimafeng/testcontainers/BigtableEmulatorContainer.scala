package com.dimafeng.testcontainers

import com.google.cloud.bigtable.admin.v2.{
  BigtableTableAdminClient,
  BigtableTableAdminSettings
}
import com.google.cloud.bigtable.data.v2.{
  BigtableDataClient,
  BigtableDataSettings
}
import org.testcontainers.containers.{
  BigtableEmulatorContainer => JavaBigtableEmulatorContainer
}
import org.testcontainers.utility.DockerImageName

class BigtableEmulatorContainer(
  bigtableEmulatorImageName: Option[DockerImageName] = None,
  projectId: String = BigtableEmulatorContainer.defaultProjectId,
  instanceId: String = BigtableEmulatorContainer.defaultInstanceId
) extends SingleContainer[JavaBigtableEmulatorContainer] {

  override val container: JavaBigtableEmulatorContainer =
    bigtableEmulatorImageName
      .map(new JavaBigtableEmulatorContainer(_))
      .getOrElse(
        new JavaBigtableEmulatorContainer(
          BigtableEmulatorContainer.defaultImageName
        )
      )

  lazy val dataClient: BigtableDataClient =
    BigtableDataClient.create(
      BigtableDataSettings
        .newBuilderForEmulator(container.getHost, container.getEmulatorPort)
        .setProjectId(projectId)
        .setInstanceId(instanceId)
        .build()
    )

  lazy val tableAdminClient: BigtableTableAdminClient =
    BigtableTableAdminClient.create(
      BigtableTableAdminSettings
        .newBuilderForEmulator(container.getHost, container.getEmulatorPort)
        .setProjectId(projectId)
        .setInstanceId(instanceId)
        .build()
    )

  def emulatorHost: String = container.getHost
  def emulatorPort: Int = container.getEmulatorPort
}

object BigtableEmulatorContainer {

  val defaultProjectId = "test-project"
  val defaultInstanceId = "test-instance"

  val defaultImageName: DockerImageName =
    DockerImageName.parse("gcr.io/google.com/cloudsdktool/cloud-sdk")

  def apply(
    bigtableEmulatorImageName: DockerImageName = null
  ): BigtableEmulatorContainer =
    new BigtableEmulatorContainer(Option(bigtableEmulatorImageName))
}
