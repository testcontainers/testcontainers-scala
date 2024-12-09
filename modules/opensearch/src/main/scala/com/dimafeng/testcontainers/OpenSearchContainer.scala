package com.dimafeng.testcontainers

import org.opensearch.testcontainers.{
  OpensearchContainer => JavaOpensearchContainer
}
import org.testcontainers.utility.DockerImageName

class OpensearchContainer(
    dockerImageName: DockerImageName,
    securityEnabled: Boolean
) extends SingleContainer[JavaOpensearchContainer] {
  override val container: JavaOpensearchContainer = {
    val c = new JavaOpensearchContainer(
      dockerImageName
    )
    if (securityEnabled)
      c.withSecurityEnabled()
    c
  }

  def username: String = container.getUsername()
  def password: String = container.getPassword()
  def isSecurityEnabled: Boolean = container.isSecurityEnabled()
  def httpHost: String =
    container.getHttpHostAddress()
}

object OpensearchContainer {
  val defaultImage: String = "opensearchproject/opensearch"
  val defaultTag: String = "2.11.0"
  val defaultDockerImageName: String = s"$defaultImage:$defaultTag"

  case class Def(
      dockerImageName: DockerImageName =
        DockerImageName.parse(defaultDockerImageName),
      securityEnabled: Boolean = false
  ) extends ContainerDef {
    override type Container = OpensearchContainer

    override protected def createContainer(): OpensearchContainer =
      new OpensearchContainer(dockerImageName, securityEnabled)
  }

  def apply(
      dockerImageNameOverride: DockerImageName = null,
      securityEnabled: Boolean = false
  ): OpensearchContainer =
    new OpensearchContainer(
      Option(dockerImageNameOverride).getOrElse(
        DockerImageName.parse(defaultDockerImageName)
      ),
      securityEnabled
    )
}
