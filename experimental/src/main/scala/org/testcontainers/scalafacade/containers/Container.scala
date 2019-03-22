package org.testcontainers.scalafacade.containers

import org.scalatest.{FreeSpec, Suite, SuiteMixin}
import org.testcontainers.containers.{GenericContainer => JavaGenericContainer}
import org.testcontainers.containers.{PostgreSQLContainer => JavaPostgreSQLContainer}
import org.testcontainers.scalafacade.containers.utils.F

trait ContainerDef[JC <: JavaGenericContainer[_], С <: Container[JC]] {
  def start: F[С]
}

trait Container[JC <: JavaGenericContainer[_]] { self =>

  protected def javaContainer: JC

  def stop: F[Unit]
}

sealed trait CList extends Product with Serializable

final case class ::[H <: ContainerDef[_, _], T <: CList](head : H, tail : T) extends CList {
  //    override def toString: String = head match {
  //      case _: ::[_, _] => "("+head+") :: "+tail.toString
  //      case _ => head+" :: "+tail.toString
  //    }
}

sealed trait CNil extends CList {
  override def toString: String = "CNil"
}

case object CNil extends CNil

class PostgreSQLContainer extends Container[JavaPostgreSQLContainer[_]] {

  protected def javaContainer: JavaPostgreSQLContainer[_] = ???

  def stop: F[Unit] = ???
}

class PostgreSQLContainerDef extends ContainerDef[JavaPostgreSQLContainer[_], PostgreSQLContainer] {
  override def start: F[PostgreSQLContainer] = ???
}

trait ForAllTestContainer[T <: CList] extends SuiteMixin { self: Suite =>

}

class MyTestSuite extends FreeSpec with ForAllTestContainer[PostgreSQLContainerDef :: PostgreSQLContainerDef :: CNil] {

}
