package com.dimafeng.testcontainers

import java.net.InetSocketAddress

import org.testcontainers.elasticsearch.{ElasticsearchContainer => JavaElasticsearchContainer}
import org.testcontainers.utility.DockerImageName

case class ElasticsearchContainer(
  dockerImageName: DockerImageName = DockerImageName.parse(ElasticsearchContainer.defaultDockerImageName)
) extends SingleContainer[JavaElasticsearchContainer] {

  override val container: JavaElasticsearchContainer = new JavaElasticsearchContainer(dockerImageName)

  def httpHostAddress: String = container.getHttpHostAddress

  def tcpHost: InetSocketAddress = container.getTcpHost
}

object ElasticsearchContainer {

  val defaultImage = "docker.elastic.co/elasticsearch/elasticsearch"
  val defaultTag = "6.4.1"
  val defaultDockerImageName = s"$defaultImage:$defaultTag"

  case class Def(
    dockerImageName: DockerImageName = DockerImageName.parse(ElasticsearchContainer.defaultDockerImageName)
  ) extends ContainerDef {

    override type Container = ElasticsearchContainer

    override def createContainer(): ElasticsearchContainer = {
      new ElasticsearchContainer(
        dockerImageName
      )
    }
  }
}
