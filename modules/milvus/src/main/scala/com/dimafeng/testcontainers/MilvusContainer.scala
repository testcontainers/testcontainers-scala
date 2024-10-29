package com.dimafeng.testcontainers

import org.testcontainers.milvus.{MilvusContainer => JavaMilvusIOContainer}
import org.testcontainers.utility.DockerImageName

case class MilvusContainer(
    dockerImageName: DockerImageName = DockerImageName.parse(MilvusContainer.defaultDockerImageName),
    etcdEndpoint: Option[String] = None
) extends SingleContainer[JavaMilvusIOContainer] {

  override val container: JavaMilvusIOContainer = {
    val c = new JavaMilvusIOContainer(dockerImageName)
    etcdEndpoint.foreach(c.withEtcdEndpoint)
    c
  }

  def endpoint: String = container.getEndpoint
}

object MilvusContainer {

  val defaultImage           = "milvusdb/milvus"
  val defaultTag             = "v2.4.4"
  val defaultDockerImageName = s"$defaultImage:$defaultTag"

  case class Def(
      dockerImageName: DockerImageName = DockerImageName.parse(MilvusContainer.defaultDockerImageName),
      etcdEndpoint: Option[String] = None
  ) extends ContainerDef {
    override type Container = MilvusContainer

    override def createContainer(): MilvusContainer = {
      new MilvusContainer(dockerImageName, etcdEndpoint)
    }
  }
}
