package org.testcontainers.testcontainers4s.containers

import org.testcontainers.containers.{GenericContainer => JavaGenericContainer}

object MyGenericContainer {

  class Def() extends GenericContainer.Def[MyGenericContainer] {
    override protected def createContainer(): MyGenericContainer = {
      new MyGenericContainer(GenericContainer.createJavaGenericContainer("foobar"))
    }

  }
}
class MyGenericContainer(
  val underlyingUnsafeContainer: JavaGenericContainer[_]
) extends GenericContainer
