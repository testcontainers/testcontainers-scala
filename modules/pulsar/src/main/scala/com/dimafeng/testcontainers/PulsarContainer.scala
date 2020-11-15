package com.dimafeng.testcontainers

import org.testcontainers.containers.{PulsarContainer => JavaPulsarContainer}
import org.testcontainers.utility.DockerImageName

case class PulsarContainer(
  dockerImageName: DockerImageName
) extends SingleContainer[JavaPulsarContainer] {

  @deprecated("Use `DockerImageName` for `dockerImageName` instead")
  def this(
    tag: String = PulsarContainer.defaultTag
  ) {
    this(
      DockerImageName.parse(PulsarContainer.defaultImage).withTag(tag)
    )
  }

  override val container: JavaPulsarContainer = new JavaPulsarContainer(dockerImageName)

  def pulsarBrokerUrl(): String = container.getPulsarBrokerUrl

  def httpServiceUrl(): String = container.getHttpServiceUrl

  @deprecated("Use `dockerImageName.getVersionPart` instead")
  def tag: String = dockerImageName.getVersionPart
}

object PulsarContainer {

  val defaultImage = "apachepulsar/pulsar"
  val defaultTag = "2.2.0"
  val defaultDockerImageName = s"$defaultImage:$defaultTag"

  case class Def(
    dockerImageName: DockerImageName = DockerImageName.parse(PulsarContainer.defaultDockerImageName)
  ) extends ContainerDef {

    override type Container = PulsarContainer

    override def createContainer(): PulsarContainer = {
      new PulsarContainer(
        dockerImageName
      )
    }
  }

}
