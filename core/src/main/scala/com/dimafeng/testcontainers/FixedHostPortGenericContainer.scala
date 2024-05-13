package com.dimafeng.testcontainers

import com.dimafeng.testcontainers.GenericContainer.FileSystemBind
import org.testcontainers.containers.startupcheck.StartupCheckStrategy
import org.testcontainers.containers.wait.strategy.WaitStrategy
import org.testcontainers.containers.{FixedHostPortGenericContainer => JavaFixedHostPortGenericContainer}

class FixedHostPortGenericContainer(imageName: String,
                                    exposedPorts: Seq[Int] = Seq(),
                                    env: Map[String, String] = Map(),
                                    command: Seq[String] = Seq(),
                                    classpathResourceMapping: Seq[FileSystemBind] = Seq(),
                                    waitStrategy: Option[WaitStrategy] = None,
                                    fileSystemBind: Seq[FileSystemBind] = Seq(),
                                    startupCheckStrategy: Option[StartupCheckStrategy] = None,
                                    portBindings: Seq[(Int, Int)] = Seq()
                                   ) extends SingleContainer[JavaFixedHostPortGenericContainer[_]] {

  override implicit val container: JavaFixedHostPortGenericContainer[_] = new JavaFixedHostPortGenericContainer(imageName)

  if (exposedPorts.nonEmpty) {
    container.withExposedPorts(exposedPorts.map(int2Integer): _*)
  }
  env.foreach { case (k, v) => container.withEnv(k, v) }
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
  startupCheckStrategy.foreach(container.withStartupCheckStrategy)
  portBindings.foreach { case (hostPort, containerPort) => container.withFixedExposedPort(hostPort, containerPort) }
}

object FixedHostPortGenericContainer {

  case class Def(imageName: String,
                 exposedPorts: Seq[Int] = Seq(),
                 env: Map[String, String] = Map(),
                 command: Seq[String] = Seq(),
                 classpathResourceMapping: Seq[FileSystemBind] = Seq(),
                 waitStrategy: Option[WaitStrategy] = None,
                 fileSystemBind: Seq[FileSystemBind] = Seq(),
                 startupCheckStrategy: Option[StartupCheckStrategy] = None,
                 portBindings: Seq[(Int, Int)]
                ) extends ContainerDef {
    override type Container = FixedHostPortGenericContainer

    override protected def createContainer(): FixedHostPortGenericContainer =
      new FixedHostPortGenericContainer(
        imageName = imageName,
        exposedPorts = exposedPorts,
        env = env,
        command = command,
        classpathResourceMapping = classpathResourceMapping,
        waitStrategy = waitStrategy,
        fileSystemBind = fileSystemBind,
        startupCheckStrategy = startupCheckStrategy,
        portBindings = portBindings
      )
  }

  def apply(
             imageName: String,
             exposedPorts: Seq[Int] = Seq(),
             env: Map[String, String] = Map(),
             command: Seq[String] = Seq(),
             classpathResourceMapping: Seq[FileSystemBind] = Seq(),
             waitStrategy: WaitStrategy = null,
             fileSystemBind: Seq[FileSystemBind] = Seq(),
             portBindings: Seq[(Int, Int)] = Seq()
           ): FixedHostPortGenericContainer =
    new FixedHostPortGenericContainer(
      imageName = imageName,
      exposedPorts = exposedPorts,
      env = env,
      command = command,
      classpathResourceMapping = classpathResourceMapping,
      fileSystemBind = fileSystemBind,
      waitStrategy = Option(waitStrategy),
      portBindings = portBindings
    )
}
