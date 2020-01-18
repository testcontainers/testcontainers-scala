package com.dimafeng.testcontainers

import java.net.InetSocketAddress

import org.testcontainers.elasticsearch.{ElasticsearchContainer => JavaElasticsearchContainer}

class ElasticsearchContainer(
  dockerImageName: String = ElasticsearchContainer.defaultDockerImageName,
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
    dockerImageName: String = ElasticsearchContainer.defaultDockerImageName,
  ) extends ContainerDef {

    override type Container = ElasticsearchContainer

    override def createContainer(): ElasticsearchContainer = {
      new ElasticsearchContainer(
        dockerImageName,
      )
    }
  }
}
