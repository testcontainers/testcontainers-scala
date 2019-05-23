package org.testcontainers.testcontainers4s.containers

import org.testcontainers.containers.{GenericContainer => JavaGenericContainer}

object MyGenericContainer {

  case class Def() extends GenericContainer.Def[MyGenericContainer](
    new MyGenericContainer(GenericContainer("foobar"))
  )
}
class MyGenericContainer(underlying: JavaGenericContainer[_]) extends GenericContainer(underlying)
