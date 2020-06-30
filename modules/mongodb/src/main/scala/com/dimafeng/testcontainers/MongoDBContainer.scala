package com.dimafeng.testcontainers

import org.testcontainers.containers.{MongoDBContainer => JavaMongoDBContainer}

case class MongoDBContainer(
  tag: String = MongoDBContainer.defaultTag
) extends SingleContainer[JavaMongoDBContainer] {

  override val container: JavaMongoDBContainer = new JavaMongoDBContainer(tag)

  def replicaSetUrl: String = container.getReplicaSetUrl
}

object MongoDBContainer {

  val defaultTag = "4.0.10"

  case class Def(
    tag: String = MongoDBContainer.defaultTag
  ) extends ContainerDef {

    override type Container = MongoDBContainer

    override def createContainer(): MongoDBContainer = new MongoDBContainer(tag)
  }
}
