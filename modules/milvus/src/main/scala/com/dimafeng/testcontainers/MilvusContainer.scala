package com.dimafeng.testcontainers

import org.testcontainers.milvus.{MilvusContainer => JavaMilvusIOContainer}
import org.testcontainers.utility.DockerImageName

case class MilvusContainer(
                            dockerImageName: DockerImageName = DockerImageName.parse(MilvusContainer.defaultDockerImageName),
                            httpPort: Int = MilvusContainer.defaultPort,
                            etcdEndpoint: Option[String] = None
) extends SingleContainer[JavaMilvusIOContainer] {

  override val container: JavaMilvusIOContainer = {
    val c = new JavaMilvusIOContainer(dockerImageName)
    c.withExposedPorts(httpPort, MilvusContainer.managementPort)
    etcdEndpoint.foreach(c.withEtcdEndpoint)
    c
  }

  def endpoint: String = container.getEndpoint
}

object MilvusContainer {

  val defaultImage = "milvusdb/milvus"
  val defaultTag = "v2.4.4"
  val defaultDockerImageName = s"$defaultImage:$defaultTag"

  val defaultPort = 19530
  val managementPort = 9091

  case class Def(dockerImageName: DockerImageName = DockerImageName.parse(MilvusContainer.defaultDockerImageName),
    port: Int = MilvusContainer.defaultPort) extends ContainerDef {
    override type Container = MilvusContainer

    override def createContainer(): MilvusContainer = {
      new MilvusContainer(dockerImageName, port)
    }
  }
}