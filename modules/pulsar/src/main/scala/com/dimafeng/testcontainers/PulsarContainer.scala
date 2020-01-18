package com.dimafeng.testcontainers

import org.testcontainers.containers.{PulsarContainer => JavaPulsarContainer}

class PulsarContainer(
  tag: String = PulsarContainer.defaultTag,
) extends SingleContainer[JavaPulsarContainer] {

  override val container: JavaPulsarContainer = new JavaPulsarContainer(tag)

  def pulsarBrokerUrl(): String = container.getPulsarBrokerUrl

  def httpServiceUrl(): String = container.getHttpServiceUrl
}

object PulsarContainer {

  val defaultTag = "2.2.0"

  case class Def(
    tag: String = PulsarContainer.defaultTag,
  ) extends ContainerDef {

    override type Container = PulsarContainer

    override def createContainer(): PulsarContainer = {
      new PulsarContainer(
        tag
      )
    }
  }

}
