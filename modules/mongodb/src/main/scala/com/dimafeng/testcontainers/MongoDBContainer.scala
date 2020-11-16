package com.dimafeng.testcontainers

import org.testcontainers.containers.{MongoDBContainer => JavaMongoDBContainer}
import org.testcontainers.utility.DockerImageName

case class MongoDBContainer(
  tag: Option[DockerImageName] = None
) extends SingleContainer[JavaMongoDBContainer] {

  override val container: JavaMongoDBContainer = tag match {
    case Some(tag) => new JavaMongoDBContainer(tag)
    case None      => new JavaMongoDBContainer()
  }

  def replicaSetUrl: String = container.getReplicaSetUrl
}

object MongoDBContainer {

  def apply(tag: DockerImageName): MongoDBContainer = new MongoDBContainer(Option(tag))

  case class Def(
    tag: DockerImageName = null
  ) extends ContainerDef {

    override type Container = MongoDBContainer

    override def createContainer(): MongoDBContainer = new MongoDBContainer(Option(tag))
  }
}
