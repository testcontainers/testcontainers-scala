package com.dimafeng.testcontainers

import org.testcontainers.containers.wait.WaitStrategy
import org.testcontainers.containers.{GenericContainer => OTCGenericContainer, BindMode}

class GenericContainer(imageName: String,
                       exposedPorts: Seq[Int] = Seq(),
                       env: Map[String, String] = Map(),
                       command: Seq[String] = Seq(),
                       classpathResourceMapping: Seq[(String, String, BindMode)] = Seq(),
                       waitStrategy: Option[WaitStrategy] = None
                      ) extends SingleContainer[OTCGenericContainer[_]] {
  override implicit val container = new OTCGenericContainer(imageName)
  if (exposedPorts.nonEmpty) {
    container.withExposedPorts(exposedPorts.map(int2Integer): _*)
  }
  env.foreach { case (key, value) => container.withEnv(key, value); Unit }
  if (command.nonEmpty) {
    container.withCommand(command: _*)
  }
  classpathResourceMapping.foreach { case (resource, containerPath, mode) =>
    container.withClasspathResourceMapping(resource, containerPath, mode)
    Unit
  }
  waitStrategy.foreach { v => container.waitingFor(v); Unit }
}

object GenericContainer {
  def apply(imageName: String,
            exposedPorts: Seq[Int] = Seq(),
            env: Map[String, String] = Map(),
            command: Seq[String] = Seq(),
            classpathResourceMapping: Seq[(String, String, BindMode)] = Seq(),
            waitStrategy: WaitStrategy = null) =
    new GenericContainer(imageName, exposedPorts, env, command, classpathResourceMapping, Option(waitStrategy))
}