package com.dimafeng.testcontainers

import java.net.URL
import org.testcontainers.nginx.{NginxContainer => JavaNginxContainer}
import org.testcontainers.utility.DockerImageName

case class NginxContainer(
    tag: DockerImageName,
) extends SingleContainer[JavaNginxContainer] {

  override val container: JavaNginxContainer = new JavaNginxContainer(tag)

  def baseUrl(scheme: String, port: Int): URL = container.getBaseUrl(scheme, port)
}

object NginxContainer {

  case class Def(
    tag: DockerImageName
  ) extends ContainerDef {

    override type Container = NginxContainer

    override def createContainer(): NginxContainer = {
      new NginxContainer(
        tag
      )
    }
  }

}
