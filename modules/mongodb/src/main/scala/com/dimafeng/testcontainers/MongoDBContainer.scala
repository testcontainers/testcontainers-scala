package com.dimafeng.testcontainers

import org.testcontainers.containers.{MongoDBContainer => JavaMongoDBContainer}

case class MongoDBContainer(
  tag: Option[String] = None
) extends SingleContainer[JavaMongoDBContainer] {

  override val container: JavaMongoDBContainer = tag match {
    case Some(tag) => new JavaMongoDBContainer(tag)
    case None      => new JavaMongoDBContainer()
  }

  def replicaSetUrl: String = container.getReplicaSetUrl
}

object MongoDBContainer {

  def apply(tag: String): MongoDBContainer = new MongoDBContainer(Option(tag))

  case class Def(
    tag: String = null
  ) extends ContainerDef {

    override type Container = MongoDBContainer

    override def createContainer(): MongoDBContainer = new MongoDBContainer(Option(tag))
  }
}
