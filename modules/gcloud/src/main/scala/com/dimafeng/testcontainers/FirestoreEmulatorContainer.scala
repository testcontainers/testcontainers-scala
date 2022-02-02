package com.dimafeng.testcontainers

import com.google.cloud.NoCredentials
import com.google.cloud.firestore.FirestoreOptions
import org.testcontainers.containers.{FirestoreEmulatorContainer => JavaFirestoreEmulatorContainer}
import org.testcontainers.utility.DockerImageName

class FirestoreEmulatorContainer(
  firestoreEmulatorImageName: Option[DockerImageName] = None
) extends SingleContainer[JavaFirestoreEmulatorContainer] {

  import FirestoreEmulatorContainer._

  override val container: JavaFirestoreEmulatorContainer =
    firestoreEmulatorImageName
      .map(new JavaFirestoreEmulatorContainer(_))
      .getOrElse(new JavaFirestoreEmulatorContainer(defaultImageName))

  def firestoreOptions: FirestoreOptions = FirestoreOptions.getDefaultInstance.toBuilder
    .setHost(container.getEmulatorEndpoint)
    .setProjectId("test-project")
    .setCredentials(NoCredentials.getInstance())
    .build()

}

object FirestoreEmulatorContainer {

  val defaultImageName: DockerImageName =
    DockerImageName.parse("gcr.io/google.com/cloudsdktool/cloud-sdk")

  def apply(
    firestoreEmulatorImageName: DockerImageName = null
  ): FirestoreEmulatorContainer =
    new FirestoreEmulatorContainer(Option(firestoreEmulatorImageName))

}
