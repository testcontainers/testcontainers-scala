package com.dimafeng.testcontainers

import org.testcontainers.containers.localstack.{LocalStackContainer => JavaLocalStackContainer}
import org.testcontainers.utility.DockerImageName

case class LocalStackContainer(
  dockerImageName: DockerImageName = DockerImageName.parse(LocalStackContainer.defaultDockerImageName),
  services: Seq[LocalStackContainer.Service] = Seq.empty
) extends SingleContainer[JavaLocalStackContainer] {

  override val container: JavaLocalStackContainer = {
    val c = new JavaLocalStackContainer(dockerImageName)
    c.withServices(services: _*)
    c
  }
}

object LocalStackContainer {

  val defaultImage = "localstack/localstack"
  val defaultTag = "0.12.12"
  val defaultDockerImageName = s"$defaultImage:$defaultTag"

  type Service = JavaLocalStackContainer.Service

  case class Def(
    dockerImageName: DockerImageName = DockerImageName.parse(LocalStackContainer.defaultDockerImageName),
    services: Seq[LocalStackContainer.Service] = Seq.empty
  ) extends ContainerDef {

    override type Container = LocalStackContainer

    override def createContainer(): LocalStackContainer = {
      new LocalStackContainer(
        dockerImageName,
        services
      )
    }
  }
}
