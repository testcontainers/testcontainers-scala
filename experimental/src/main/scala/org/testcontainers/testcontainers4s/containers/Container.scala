package org.testcontainers.testcontainers4s.containers

import org.testcontainers.containers.{GenericContainer => JavaGenericContainer}

trait Container[JC <: JavaGenericContainer[_]] extends ContainerList { self =>

  def underlyingUnsafeContainer: JC

  def stop(): Unit = underlyingUnsafeContainer.stop()
}

trait ContainerDef[JC <: JavaGenericContainer[_], С <: Container[JC]] extends ContainerDefList {

  override type Containers = С

  protected def createContainer: С

  def start(): С = {
    val container = createContainer
    container.underlyingUnsafeContainer.start()
    container
  }
}


sealed trait ContainerDefList {
  type Containers <: ContainerList
}

final case class andDef[D1 <: ContainerDefList, D2 <: ContainerDefList](head : D1, tail : D2) extends ContainerDefList {
  override type Containers = D1#Containers and D2#Containers
}

sealed trait ContainerList {
  def stop(): Unit
}
final case class and[C1 <: ContainerList, C2 <: ContainerList](head : C1, tail : C2) extends ContainerList {
  override def stop(): Unit = {
    // TODO: test stopping order
    head.stop()
    tail.stop()
  }
}

object ContainerList {
  implicit class ContainerListOps[T <: ContainerList](val self: T) extends AnyVal {
    def and[T2 <: ContainerList](that: T2): T and T2 = org.testcontainers.testcontainers4s.containers.and(self, that)
  }
}
