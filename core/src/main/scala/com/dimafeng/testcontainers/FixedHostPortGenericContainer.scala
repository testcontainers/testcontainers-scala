package com.dimafeng.testcontainers

import com.dimafeng.testcontainers.GenericContainer.FileSystemBind
import org.testcontainers.containers.wait.strategy.WaitStrategy
import org.testcontainers.containers.{BindMode, FixedHostPortGenericContainer => JavaFixedHostPortGenericContainer}

class FixedHostPortGenericContainer(imageName: String,
                                    exposedPorts: Seq[Int] = Seq(),
                                    env: Map[String, String] = Map(),
                                    command: Seq[String] = Seq(),
                                    classpathResourceMapping: Seq[FileSystemBind] = Seq(),
                                    waitStrategy: Option[WaitStrategy] = None,
                                    exposedHostPort: Int,
                                    exposedContainerPort: Int,
                                    fileSystemBind: Seq[FileSystemBind] = Seq()
                                   ) extends SingleContainer[JavaFixedHostPortGenericContainer[_]] {

  override implicit val container: JavaFixedHostPortGenericContainer[_] = new JavaFixedHostPortGenericContainer(imageName)

  if (exposedPorts.nonEmpty) {
    container.withExposedPorts(exposedPorts.map(int2Integer): _*)
  }
  env.foreach{ case (k, v) => container.withEnv(k, v) }
  if (command.nonEmpty) {
    container.withCommand(command: _*)
  }
  classpathResourceMapping.foreach {
    case FileSystemBind(hostFilePath, containerFilePath, bindMode) =>
      container.withClasspathResourceMapping(hostFilePath, containerFilePath, bindMode)
  }
  fileSystemBind.foreach {
    case FileSystemBind(hostFilePath, containerFilePath, bindMode) =>
      container.withFileSystemBind(hostFilePath, containerFilePath, bindMode)
  }
  waitStrategy.foreach(container.waitingFor)
  container.withFixedExposedPort(exposedHostPort, exposedContainerPort)
}

object FixedHostPortGenericContainer {
  def apply(
    imageName: String,
    exposedPorts: Seq[Int] = Seq(),
    env: Map[String, String] = Map(),
    command: Seq[String] = Seq(),
    classpathResourceMapping: Seq[FileSystemBind] = Seq(),
    waitStrategy: WaitStrategy = null,
    exposedHostPort: Int,
    exposedContainerPort: Int,
    fileSystemBind: Seq[FileSystemBind] = Seq()
  ): FixedHostPortGenericContainer=
    new FixedHostPortGenericContainer(
      imageName = imageName,
      exposedPorts = exposedPorts,
      env = env,
      command = command,
      classpathResourceMapping = classpathResourceMapping,
      fileSystemBind = fileSystemBind,
      waitStrategy = Option(waitStrategy),
      exposedHostPort = exposedHostPort,
      exposedContainerPort = exposedContainerPort
    )
}
