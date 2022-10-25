package com.dimafeng.testcontainers

import org.testcontainers.redpanda.{RedpandaContainer => JavaRedpandaContainer}
import org.testcontainers.utility.DockerImageName

case class RedpandaContainer(
  dockerImageName: DockerImageName = DockerImageName.parse(RedpandaContainer.defaultDockerImageName)
) extends SingleContainer[JavaRedpandaContainer] {

  override val container: JavaRedpandaContainer = new JavaRedpandaContainer(dockerImageName)

  def bootstrapServers: String = container.getBootstrapServers
}

object RedpandaContainer {

  val defaultImage = "docker.redpanda.com/vectorized/redpanda"
  val defaultTag = "v22.2.6"
  val defaultDockerImageName = s"$defaultImage:$defaultTag"

  case class Def(dockerImageName: DockerImageName = DockerImageName.parse(RedpandaContainer.defaultDockerImageName)
                ) extends ContainerDef {

    override type Container = RedpandaContainer

    override def createContainer(): RedpandaContainer = {
      new RedpandaContainer(dockerImageName)
    }
  }
}
