package com.dimafeng.testcontainers

import com.dimafeng.testcontainers.lifecycle.Stoppable
import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.command.InspectContainerResponse
import com.github.dockerjava.api.model.{Bind, VolumesFrom}
import org.testcontainers.containers.output.OutputFrame
import org.testcontainers.containers.startupcheck.StartupCheckStrategy
import org.testcontainers.containers.traits.LinkableContainer
import org.testcontainers.containers.{Network, Container => JavaContainer, GenericContainer => JavaGenericContainer}
import org.testcontainers.core.CreateContainerCmdModifier
import org.testcontainers.images.builder.Transferable
import org.testcontainers.lifecycle.Startable
import org.testcontainers.utility.{MountableFile, ThrowingFunction}

import java.io.InputStream
import java.nio.charset.Charset
import java.util.function.Consumer
import scala.concurrent.{Future, blocking}

import scala.collection.JavaConverters._

@deprecated("For internal usage only. Will be deleted.", "v0.34.0")
trait TestContainerProxy[T] extends Container {

  @deprecated("Please use reflective methods from the wrapper and `configure` method for creation", "v0.17.0")
  implicit def container: T
  
}

abstract class SingleContainer[T <: JavaGenericContainer[?]] extends TestContainerProxy[T] {

  def underlyingUnsafeContainer: T = container

  override def start(): Unit = container.start()

  override def stop(): Unit = container.stop()

  def binds: Seq[Bind] = container.getBinds.asScala.toSeq

  def command: Seq[String] = container.getCommandParts

  def containerId: String = container.getContainerId

  def containerInfo: InspectContainerResponse = container.getContainerInfo

  def containerIpAddress: String = container.getContainerIpAddress

  def host: String = container.getHost

  def containerName: String = container.getContainerName

  def env: Seq[String] = container.getEnv.asScala.toSeq

  def envMap: Map[String, String] = container.getEnvMap.asScala.toMap

  def exposedPorts: Seq[Int] = container.getExposedPorts.asScala.toSeq.map(_.intValue())

  def extraHosts: Seq[String] = container.getExtraHosts.asScala.toSeq

  import scala.concurrent.ExecutionContext.Implicits.global

  def image: Future[String] = Future {
    blocking {
      container.getImage.get()
    }
  }

  @deprecated("See org.testcontainers.containers.Network", "v0.17.0")
  def linkedContainers: Map[String, LinkableContainer] = container.getLinkedContainers.asScala.toMap

  def mappedPort(port: Int): Int = container.getMappedPort(port)

  def firstMappedPort: Int = container.getFirstMappedPort()

  def portBindings: Seq[String] = container.getPortBindings.asScala.toSeq

  def boundPortNumbers: Seq[Int] = container.getBoundPortNumbers.asScala.toSeq.map(_.intValue())

  def networkMode: String = container.getNetworkMode

  def network: Network = container.getNetwork

  def networkAliases: Seq[String] = container.getNetworkAliases.asScala.toSeq

  def privilegedMode: Boolean = container.isPrivilegedMode

  def volumesFroms: Seq[VolumesFrom] = container.getVolumesFroms.asScala.toSeq

  def startupCheckStrategy: StartupCheckStrategy = container.getStartupCheckStrategy

  def startupAttempts: Int = container.getStartupAttempts

  def workingDirectory: String = container.getWorkingDirectory

  def dockerClient: DockerClient = container.getDockerClient

  def logConsumers: Seq[Consumer[OutputFrame]] = container.getLogConsumers.asScala.toSeq

  def createContainerCmdModifiers: Set[CreateContainerCmdModifier] = container.getCreateContainerCmdModifiers.asScala.toSet

  def copyToFileContainerPathMap: Map[MountableFile, String] = container.getCopyToFileContainerPathMap.asScala.toMap

  def labels: Map[String, String] = container.getLabels.asScala.toMap

  def shmSize: Long = container.getShmSize

  def testHostIpAddress: String = container.getTestHostIpAddress

  def tmpFsMapping: Map[String, String] = container.getTmpFsMapping.asScala.toMap

  def logs: String = container.getLogs

  def logs(outputType: OutputFrame.OutputType, outputTypes: OutputFrame.OutputType*): String =
    container.getLogs((outputType +: outputTypes): _*)

  def livenessCheckPortNumbers: Set[Int] = container.getLivenessCheckPortNumbers.asScala.toSet.map { (x: java.lang.Integer) =>
    x.intValue()
  }

  def execInContainer(commands: String*): JavaContainer.ExecResult = {
    container.execInContainer(commands: _*)
  }

  def execInContainer(outputCharset: Charset, commands: String*): JavaContainer.ExecResult = {
    container.execInContainer(outputCharset, commands: _*)
  }

  def copyFileToContainer(mountableFile: MountableFile, containerPath: String): Unit = {
    container.copyFileToContainer(mountableFile, containerPath)
  }

  def copyFileToContainer(transferable: Transferable, containerPath: String): Unit = {
    container.copyFileToContainer(transferable, containerPath)
  }

  def copyFileFromContainer(containerPath: String, destinationPath: String): Unit = {
    container.copyFileFromContainer(containerPath, destinationPath)
  }

  def copyFileFromContainer[T](containerPath: String, f: InputStream => T): T = {
    container.copyFileFromContainer(containerPath, new ThrowingFunction[InputStream, T] {
      override def apply(inputStream: InputStream): T = f(inputStream)
    })
  }

  def configure(configProvider: T => Unit): this.type = {
    configProvider(container)
    this
  }
}

trait Container extends Startable with Stoppable {

}
