package com.dimafeng.testcontainers

import org.testcontainers.images.builder.Transferable
import org.testcontainers.qdrant.{QdrantContainer => JavaQdrantContainer}
import org.testcontainers.utility.DockerImageName

class QdrantContainer(
    underlying: JavaQdrantContainer
) extends SingleContainer[JavaQdrantContainer] { self =>

  override val container: JavaQdrantContainer = underlying

  def grpcPort: Int = container.getGrpcPort

  def grpcHostAddress: String = container.getGrpcHostAddress

}

object QdrantContainer {

  val defaultImage           = "qdrant/qdrant"
  val defaultTag             = "v1.12.1"
  val defaultDockerImageName = s"$defaultImage:$defaultTag"

  case class Def(
      dockerImageName: DockerImageName = DockerImageName.parse(QdrantContainer.defaultDockerImageName),
      builder: List[JavaQdrantContainer => JavaQdrantContainer] = List.empty
  ) extends ContainerDef {
    override type Container = QdrantContainer

    def withApiKey(apiKey: String): Def =
      copy(builder = ((_: JavaQdrantContainer).withApiKey(apiKey)) :: builder)

    def withConfigFile(configFile: Transferable): Def =
      copy(builder = ((_: JavaQdrantContainer).withConfigFile(configFile)) :: builder)

    override def createContainer(): QdrantContainer =
      new QdrantContainer(
        builder
          .foldRight(new JavaQdrantContainer(dockerImageName))((f, underlying) => f(underlying))
      )
  }
}
