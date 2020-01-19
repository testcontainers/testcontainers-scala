package com.dimafeng.testcontainers

import java.net.URL

import org.testcontainers.containers.{NginxContainer => JavaNginxContainer}

case class NginxContainer(
  customContent: Option[String] = None
) extends SingleContainer[JavaNginxContainer[_]] {

  override val container: JavaNginxContainer[_] = {
    val c = new JavaNginxContainer()
    customContent.foreach(c.withCustomContent)
    c
  }

  def baseUrl(scheme: String, port: Int): URL = container.getBaseUrl(scheme, port)
}

object NginxContainer {

  case class Def(
    customContent: Option[String] = None
  ) extends ContainerDef {

    override type Container = NginxContainer

    override def createContainer(): NginxContainer = {
      new NginxContainer(
        customContent
      )
    }
  }

}
