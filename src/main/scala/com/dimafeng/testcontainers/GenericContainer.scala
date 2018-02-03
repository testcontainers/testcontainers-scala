package com.dimafeng.testcontainers

import org.testcontainers.containers.wait.WaitStrategy
import org.testcontainers.containers.{BindMode, GenericContainer => OTCGenericContainer}

class GenericContainer(imageName: String,
                       exposedPorts: Seq[Int] = Seq(),
                       env: Map[String, String] = Map(),
                       command: Seq[String] = Seq(),
                       classpathResourceMapping: Seq[(String, String, BindMode)] = Seq(),
                       waitStrategy: Option[WaitStrategy] = None
                      ) extends SingleContainer[OTCGenericContainer[_]] {

  type OTCContainer = OTCGenericContainer[T] forSome {type T <: OTCGenericContainer[T]}
  override implicit val container: OTCContainer = new OTCGenericContainer(imageName)

  if (exposedPorts.nonEmpty) {
    container.withExposedPorts(exposedPorts.map(int2Integer): _*)
  }
  env.foreach(Function.tupled(container.withEnv))
  if (command.nonEmpty) {
    container.withCommand(command: _*)
  }
  classpathResourceMapping.foreach(Function.tupled(container.withClasspathResourceMapping))
  waitStrategy.foreach(container.waitingFor)
}

object GenericContainer {
  def apply(imageName: String,
            exposedPorts: Seq[Int] = Seq(),
            env: Map[String, String] = Map(),
            command: Seq[String] = Seq(),
            classpathResourceMapping: Seq[(String, String, BindMode)] = Seq(),
            waitStrategy: WaitStrategy = null): GenericContainer =
    new GenericContainer(imageName, exposedPorts, env, command, classpathResourceMapping, Option(waitStrategy))
}