package com.dimafeng.testcontainers

import org.testcontainers.containers.wait.strategy.WaitStrategy
import org.testcontainers.containers.{BindMode, FixedHostPortGenericContainer => JavaFixedHostPortGenericContainer}

class FixedHostPortGenericContainer(imageName: String,
                                    exposedPorts: Seq[Int] = Seq(),
                                    env: Map[String, String] = Map(),
                                    command: Seq[String] = Seq(),
                                    classpathResourceMapping: Seq[(String, String, BindMode)] = Seq(),
                                    waitStrategy: Option[WaitStrategy] = None,
                                    exposedHostPort: Int,
                                    exposedContainerPort: Int
                                   ) extends SingleContainer[JavaFixedHostPortGenericContainer[_]] {

  override implicit val container: JavaFixedHostPortGenericContainer[_] = new JavaFixedHostPortGenericContainer(imageName)

  if (exposedPorts.nonEmpty) {
    container.withExposedPorts(exposedPorts.map(int2Integer): _*)
  }
  env.foreach{case (k, v) => container.withEnv(k, v)}
  if (command.nonEmpty) {
    container.withCommand(command: _*)
  }
  classpathResourceMapping.foreach{case (a, b, c) => container.withClasspathResourceMapping(a, b, c)}
  waitStrategy.foreach(container.waitingFor)
  container.withFixedExposedPort(exposedHostPort, exposedContainerPort)
}

object FixedHostPortGenericContainer {
  def apply(imageName: String,
            exposedPorts: Seq[Int] = Seq(),
            env: Map[String, String] = Map(),
            command: Seq[String] = Seq(),
            classpathResourceMapping: Seq[(String, String, BindMode)] = Seq(),
            waitStrategy: WaitStrategy = null,
            exposedHostPort: Int,
            exposedContainerPort: Int): FixedHostPortGenericContainer=
    new FixedHostPortGenericContainer(imageName,
      exposedPorts,
      env,
      command,
      classpathResourceMapping,
      Option(waitStrategy),
      exposedHostPort,
      exposedContainerPort
    )
}
