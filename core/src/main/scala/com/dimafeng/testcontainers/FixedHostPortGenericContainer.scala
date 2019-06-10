package com.dimafeng.testcontainers

import org.testcontainers.containers.wait.strategy.WaitStrategy
import org.testcontainers.containers.{BindMode, FixedHostPortGenericContainer => OTCFixedHostPortGenericContainer}

class FixedHostPortGenericContainer(imageName: String,
                                    exposedPorts: Seq[Int] = Seq(),
                                    env: Map[String, String] = Map(),
                                    command: Seq[String] = Seq(),
                                    classpathResourceMapping: Seq[(String, String, BindMode)] = Seq(),
                                    waitStrategy: Option[WaitStrategy] = None,
                                    exposedHostPort: Int,
                                    exposedContainerPort: Int
                                   ) extends SingleContainer[OTCFixedHostPortGenericContainer[_]] {

  type FixedHostPortContainer = OTCFixedHostPortGenericContainer[T] forSome {type T <: OTCFixedHostPortGenericContainer[T]}
  override implicit val container: FixedHostPortContainer = new OTCFixedHostPortGenericContainer(imageName)

  if (exposedPorts.nonEmpty) {
    container.withExposedPorts(exposedPorts.map(int2Integer): _*)
  }
  env.foreach(Function.tupled(container.withEnv))
  if (command.nonEmpty) {
    container.withCommand(command: _*)
  }
  classpathResourceMapping.foreach(Function.tupled(container.withClasspathResourceMapping))
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
