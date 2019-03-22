package org.testcontainers.scalafacade.containers

import org.scalatest.{FreeSpec, Suite, SuiteMixin}
import org.testcontainers.containers.{GenericContainer => JavaGenericContainer}
import org.testcontainers.containers.{PostgreSQLContainer => JavaPostgreSQLContainer}
import org.testcontainers.scalafacade.containers.utils.F

sealed trait ContainerDefList {
  type Containers <: ContainerList
}
final case class andDef[D1 <: ContainerDefList, D2 <: ContainerDefList](head : D1, tail : D2) extends ContainerDefList {
  override type Containers = D1#Containers and D2#Containers
}

sealed trait ContainerList
final case class and[C1 <: ContainerList, C2 <: ContainerList](head : C1, tail : C2) extends ContainerList

trait ContainerDef[JC <: JavaGenericContainer[_], С <: Container[JC]] extends ContainerDefList {

  override type Containers = С

  def start: С
}

trait Container[JC <: JavaGenericContainer[_]] extends ContainerList { self =>

  protected def javaContainer: JC

  def stop: Unit
}

class PostgreSQLContainer extends Container[JavaPostgreSQLContainer[_]] {

  protected def javaContainer: JavaPostgreSQLContainer[_] = ???

  def hehe: Unit = ???

  def stop: Unit = ???
}

class PostgreSQLContainerDef extends ContainerDef[JavaPostgreSQLContainer[_], PostgreSQLContainer] {
  override def start: PostgreSQLContainer = ???
}

trait ForAllTestContainer[C <: ContainerDefList] extends SuiteMixin { self: Suite =>

  def startContainers: F[C#Containers]

  def withContainers(containers: C#Containers => Unit): Unit = {
    ???
  }
}

class MyTestSuite extends FreeSpec with ForAllTestContainer[PostgreSQLContainerDef andDef PostgreSQLContainerDef andDef PostgreSQLContainerDef] {

  override def startContainers = {
    ???
  }

  "foo" - {
    "bar" in withContainers { case pg1 and pg2 and pg3 =>
      pg1.hehe
    }
  }
}

class MyTestSuite2 extends FreeSpec with ForAllTestContainer[PostgreSQLContainerDef] {

  override def startContainers = {
    ???
  }

  "foo" - {
    "bar" in withContainers { case pg1 =>
      pg1.hehe
    }
  }
}
