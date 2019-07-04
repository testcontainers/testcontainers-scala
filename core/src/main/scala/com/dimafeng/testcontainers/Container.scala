package com.dimafeng.testcontainers

import java.util.function.Consumer

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.command.{CreateContainerCmd, InspectContainerResponse}
import com.github.dockerjava.api.model.{Bind, Info, VolumesFrom}
import org.junit.runner.Description
import org.testcontainers.containers.output.OutputFrame
import org.testcontainers.containers.startupcheck.StartupCheckStrategy
import org.testcontainers.containers.traits.LinkableContainer
import org.testcontainers.containers.{FailureDetectingExternalResource, Network, TestContainerAccessor, GenericContainer => OTCGenericContainer}
import org.testcontainers.lifecycle.Startable

import scala.collection.JavaConverters._
import scala.concurrent.{Future, blocking}

trait TestContainerProxy[T <: FailureDetectingExternalResource] extends Container {

  @deprecated("Please use reflective methods from the wrapper and `configure` method for creation")
  implicit def container: T

  @deprecated("Use `stop` instead")
  override def finished()(implicit description: Description): Unit = TestContainerAccessor.finished(description)

  @deprecated("Use `stop` and/or `TestLifecycleAware.afterTest` instead")
  override def succeeded()(implicit description: Description): Unit = TestContainerAccessor.succeeded(description)

  @deprecated("Use `start` instead")
  override def starting()(implicit description: Description): Unit = TestContainerAccessor.starting(description)

  @deprecated("Use `stop` and/or `TestLifecycleAware.afterTest` instead")
  override def failed(e: Throwable)(implicit description: Description): Unit = TestContainerAccessor.failed(e, description)
}

abstract class SingleContainer[T <: OTCGenericContainer[_]] extends TestContainerProxy[T] {

  override def start(): Unit = container.start()

  override def stop(): Unit = container.stop()

  def binds: Seq[Bind] = container.getBinds.asScala.toSeq

  def command: Seq[String] = container.getCommandParts

  def containerId: String = container.getContainerId

  def containerInfo: InspectContainerResponse = container.getContainerInfo

  def containerIpAddress: String = container.getContainerIpAddress

  def containerName: String = container.getContainerName

  def env: Seq[String] = container.getEnv.asScala.toSeq

  def exposedPorts: Seq[Int] = container.getExposedPorts.asScala.toSeq.map(_.intValue())

  def extraHosts: Seq[String] = container.getExtraHosts.asScala.toSeq

  import scala.concurrent.ExecutionContext.Implicits.global

  def image: Future[String] = Future {
    blocking {
      container.getImage.get()
    }
  }

  @deprecated("See org.testcontainers.containers.Network")
  def linkedContainers: Map[String, LinkableContainer] = container.getLinkedContainers.asScala.toMap

  def mappedPort(port: Int): Int = container.getMappedPort(port)

  def portBindings: Seq[String] = container.getPortBindings.asScala.toSeq

  def networkMode: String = container.getNetworkMode

  def network: Network = container.getNetwork

  def networkAliases: Seq[String] = container.getNetworkAliases.asScala.toSeq

  def privilegedMode: Boolean = container.isPrivilegedMode

  def volumesFroms: Seq[VolumesFrom] = container.getVolumesFroms.asScala.toSeq

  def startupCheckStrategy: StartupCheckStrategy = container.getStartupCheckStrategy

  def startupAttempts: Int = container.getStartupAttempts

  def workingDirectory: String = container.getWorkingDirectory

  def dockerClient: DockerClient = container.getDockerClient

  def dockerDaemonInfo: Info = container.getDockerDaemonInfo

  def logConsumers: Seq[Consumer[OutputFrame]] = container.getLogConsumers.asScala.toSeq

  def createContainerCmdModifiers: Set[Consumer[CreateContainerCmd]] = container.getCreateContainerCmdModifiers.asScala.toSet

  def configure(configProvider: T => Unit): this.type = {
    configProvider(container)
    this
  }
}

trait Container extends Startable {

  @deprecated("Use `stop` instead")
  def finished()(implicit description: Description): Unit = stop()

  @deprecated("Use `stop` and/or `TestLifecycleAware.afterTest` instead")
  def failed(e: Throwable)(implicit description: Description): Unit = {}

  @deprecated("Use `start` instead")
  def starting()(implicit description: Description): Unit = start()

  @deprecated("Use `stop` and/or `TestLifecycleAware.afterTest` instead")
  def succeeded()(implicit description: Description): Unit = {}
}
