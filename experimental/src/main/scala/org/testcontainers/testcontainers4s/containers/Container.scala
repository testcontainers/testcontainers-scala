package org.testcontainers.testcontainers4s.containers

import org.scalatest.{FreeSpec, Suite, SuiteMixin}
import org.testcontainers.containers.{GenericContainer => JavaGenericContainer}

sealed trait ContainerDefList {
  type Containers <: ContainerList
}
final case class andDef[D1 <: ContainerDefList, D2 <: ContainerDefList](head : D1, tail : D2) extends ContainerDefList {
  override type Containers = D1#Containers and D2#Containers
}

sealed trait ContainerList
final case class and[C1 <: ContainerList, C2 <: ContainerList](head : C1, tail : C2) extends ContainerList

object ContainerList {
  implicit class ContainerListOps[T <: ContainerList](val self: T) extends AnyVal {
    def and[T2 <: ContainerList](that: T2): T and T2 = org.testcontainers.testcontainers4s.containers.and(self, that)
  }
}

trait ContainerDef[JC <: JavaGenericContainer[_], С <: Container[JC]] extends ContainerDefList {

  override type Containers = С

  protected def createContainer: С

  def start: С = {
    val container = createContainer
    container.underlyingUnsafeContainer.start()
    container
  }
}

trait Container[JC <: JavaGenericContainer[_]] extends ContainerList { self =>

  def underlyingUnsafeContainer: JC

  def stop: Unit = ???
}

trait ForAllTestContainer[C <: ContainerDefList] extends SuiteMixin { self: Suite =>

  def startContainers: C#Containers

  def withContainers(containers: C#Containers => Unit): Unit = {
    ???
  }
}

class MyTestSuite extends FreeSpec with ForAllTestContainer[PostgreSQLContainer.Def andDef MySQLContainer.Def] {

  override def startContainers = {
    val pg = new PostgreSQLContainer.Def().start
    val mySql = new MySQLContainer.Def().start

    pg and mySql
  }

  "foo" - {
    "bar" in withContainers { case pg and mySql =>
      assert(pg.jdbcUrl.nonEmpty && mySql.jdbcUrl.nonEmpty)
    }
  }
}

class MyTestSuite2 extends FreeSpec with ForAllTestContainer[PostgreSQLContainer.Def] {

  override def startContainers = {
    new PostgreSQLContainer.Def().start
  }

  "foo" - {
    "bar" in withContainers { pg1 =>
      assert(pg1.jdbcUrl.nonEmpty)
    }
  }
}
