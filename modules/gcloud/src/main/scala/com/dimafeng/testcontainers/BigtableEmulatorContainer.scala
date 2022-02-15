package com.dimafeng.testcontainers

import com.dimafeng.testcontainers.FirestoreEmulatorContainer.defaultImageName
import com.google.cloud.bigtable.admin.v2.{
  BigtableInstanceAdminClient,
  BigtableInstanceAdminSettings,
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
  projectId: String = BigtableEmulatorContainer.defaultProjectId
) extends SingleContainer[JavaBigtableEmulatorContainer] {

  override val container: JavaBigtableEmulatorContainer =
    bigtableEmulatorImageName
      .map(new JavaBigtableEmulatorContainer(_))
      .getOrElse(new JavaBigtableEmulatorContainer(defaultImageName))

  def dataClient: BigtableDataClient =
    BigtableDataClient.create(
      BigtableDataSettings
        .newBuilderForEmulator(container.getHost, container.getEmulatorPort)
        .setProjectId(projectId)
        .setInstanceId("value-not-important")
        .build()
    )

  def tableAdminClient: BigtableTableAdminClient =
    BigtableTableAdminClient.create(
      BigtableTableAdminSettings
        .newBuilderForEmulator(container.getHost, container.getEmulatorPort)
        .setProjectId(projectId)
        .setInstanceId("value-not-important")
        .build()
    )
}

object BigtableEmulatorContainer {

  val defaultProjectId = "test-project"

  val defaultImageName: DockerImageName =
    DockerImageName.parse("gcr.io/google.com/cloudsdktool/cloud-sdk")

  def apply(
    bigtableEmulatorImageName: DockerImageName = null
  ): BigtableEmulatorContainer =
    new BigtableEmulatorContainer(Option(bigtableEmulatorImageName))
}
