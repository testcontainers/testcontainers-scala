package org.testcontainers.scalafacade.containers

import org.testcontainers.containers.{GenericContainer => JavaGenericContainer}
import org.testcontainers.scalafacade.containers.utils.F

trait ContainerDefinition[JC <: JavaGenericContainer[_], SC <: StartedContainer[JC]] {
  def start: F[SC]
}

trait StartedContainer[JC <: JavaGenericContainer[_]] {

  protected def javaContainer: JC

  def stop: F[Unit]
}
