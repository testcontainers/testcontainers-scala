package com.dimafeng.testcontainers

import org.testcontainers.containers.{MongoDBContainer => JavaMongoDBContainer}
import org.testcontainers.utility.DockerImageName

case class MongoDBContainer(
  tag: Option[DockerImageName] = None
) extends SingleContainer[JavaMongoDBContainer] {
  private val defaultImageName = DockerImageName.parse("mongo")
  private val defaultMongoTag = "4.0.10"

  override val container: JavaMongoDBContainer = tag match {
    case Some(tag) => new JavaMongoDBContainer(tag)
    case None      => new JavaMongoDBContainer(defaultImageName.withTag(defaultMongoTag))
  }

  def replicaSetUrl: String = container.getReplicaSetUrl
}

object MongoDBContainer {
  case class Def(
    tag: Option[DockerImageName]
  ) extends ContainerDef {

    override type Container = MongoDBContainer

    override def createContainer(): MongoDBContainer = new MongoDBContainer(tag)
  }
}
