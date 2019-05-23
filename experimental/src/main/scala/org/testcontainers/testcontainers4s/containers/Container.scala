package org.testcontainers.testcontainers4s.containers

import org.testcontainers.containers.{GenericContainer => JavaGenericContainer}

trait Container extends ContainerList {

  type JavaContainer <: JavaGenericContainer[_]

  def underlyingUnsafeContainer: JavaContainer

  def stop(): Unit = underlyingUnsafeContainer.stop()
}
object Container {
  type Aux[JC <: JavaGenericContainer[_]] = Container { type JavaContainer = JC }
}

trait ContainerDef extends ContainerDefList {

  type Container <: org.testcontainers.testcontainers4s.containers.Container

  override type Containers = Container

  protected def createContainer(): Container

  def start(): Container = {
    val container = createContainer()
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

  def foreach(f: Container => Unit): Unit = {
    // TODO: test it
    this match {
      case and(head, tail) =>
        head.foreach(f)
        tail.foreach(f)

      case container: Container =>
        f(container)
    }
  }

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
