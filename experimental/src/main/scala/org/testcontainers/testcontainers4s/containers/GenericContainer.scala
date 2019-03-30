package org.testcontainers.testcontainers4s.containers

import org.testcontainers.containers.BindMode
import org.testcontainers.containers.wait.strategy.WaitStrategy
import org.testcontainers.containers.{GenericContainer => JavaGenericContainer}

object GenericContainer {

  def createJavaGenericContainer(
    dockerImageName: String, // TODO: Future[String] like in the old implementation?
    exposedPorts: List[Int] = List.empty,
    env: Map[String, String] = Map.empty,
    command: List[String] = List.empty,
    classpathResourceMapping: List[(String, String, BindMode)] = List.empty,
    waitStrategy: Option[WaitStrategy] = None
  ): JavaGenericContainer[_] = {
    val javaContainer = new JavaGenericContainer(dockerImageName)

    if (exposedPorts.nonEmpty) {
      javaContainer.withExposedPorts(exposedPorts.map(int2Integer): _*)
    }
    env.foreach(Function.tupled(javaContainer.withEnv))
    if (command.nonEmpty) {
      javaContainer.withCommand(command: _*)
    }
    classpathResourceMapping.foreach(Function.tupled(javaContainer.withClasspathResourceMapping))
    waitStrategy.foreach(javaContainer.waitingFor)

    javaContainer
  }

  abstract class Def[C <: GenericContainer](init: => C) extends ContainerDef[JavaGenericContainer[_], C] {
    protected def createContainer(): C = init
  }
}
trait GenericContainer extends Container[JavaGenericContainer[_]]
