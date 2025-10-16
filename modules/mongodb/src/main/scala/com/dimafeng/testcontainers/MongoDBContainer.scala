package com.dimafeng.testcontainers

import org.testcontainers.mongodb.{MongoDBContainer => JavaMongoDBContainer}
import org.testcontainers.utility.DockerImageName

case class MongoDBContainer(
  tag: DockerImageName
) extends SingleContainer[JavaMongoDBContainer] {

  override val container: JavaMongoDBContainer = new JavaMongoDBContainer(tag)

  def replicaSetUrl: String = container.getReplicaSetUrl
}

object MongoDBContainer {

  def apply(tag: DockerImageName): MongoDBContainer = new MongoDBContainer(tag)

  case class Def(
    tag: DockerImageName
  ) extends ContainerDef {

    override type Container = MongoDBContainer

    override def createContainer(): MongoDBContainer = new MongoDBContainer(tag)
  }
}
