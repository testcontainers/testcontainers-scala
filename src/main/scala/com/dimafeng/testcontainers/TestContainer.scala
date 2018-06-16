package com.dimafeng.testcontainers

import java.io.File
import java.util.function.Consumer

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.command.{CreateContainerCmd, InspectContainerResponse}
import com.github.dockerjava.api.model.{Bind, Info, VolumesFrom}
import org.junit.runner.Description
import org.scalatest._
import org.testcontainers.containers.output.OutputFrame
import org.testcontainers.containers.startupcheck.StartupCheckStrategy
import org.testcontainers.containers.traits.LinkableContainer
import org.testcontainers.containers.wait.strategy.{Wait, WaitStrategy}
import org.testcontainers.containers.{FailureDetectingExternalResource, Network, TestContainerAccessor}
import org.testcontainers.containers.{DockerComposeContainer => OTCDockerComposeContainer, GenericContainer => OTCGenericContainer}
import org.testcontainers.utility.Base58

import scala.collection.JavaConverters._
import scala.concurrent.{Future, blocking}

trait ForEachTestContainer extends SuiteMixin {
  self: Suite =>

  val container: Container

  implicit private val suiteDescription = Description.createSuiteDescription(self.getClass)

  abstract protected override def runTest(testName: String, args: Args): Status = {
    container.starting()
    try {
      afterStart()
      val status = super.runTest(testName, args)
      status match {
        case FailedStatus => container.failed(new RuntimeException(status.toString))
        case _ => container.succeeded()
      }
      status
    }
    catch {
      case e: Throwable =>
        container.failed(e)
        throw e
    }
    finally {
      try {
        beforeStop()
      }
      finally {
        container.finished()
      }
    }
  }

  def afterStart(): Unit = {}

  def beforeStop(): Unit = {}
}

trait ForAllTestContainer extends SuiteMixin {
  self: Suite =>

  val container: Container

  implicit private val suiteDescription = Description.createSuiteDescription(self.getClass)

  abstract override def run(testName: Option[String], args: Args): Status = {
    if (expectedTestCount(args.filter) == 0) {
      new CompositeStatus(Set.empty)
    } else {
      container.starting()
      try {
        afterStart()
        super.run(testName, args)
      } finally {
        try {
          beforeStop()
        }
        finally {
          container.finished()
        }
      }
    }
  }

  def afterStart(): Unit = {}

  def beforeStop(): Unit = {}
}

trait Container {
  def finished()(implicit description: Description): Unit

  def failed(e: Throwable)(implicit description: Description): Unit

  def starting()(implicit description: Description): Unit

  def succeeded()(implicit description: Description): Unit
}

final case class DockerComposeExposedService(name: String, port: Int, waitStrategy: WaitStrategy = Wait.defaultWaitStrategy(), instance: Option[Int] = None)

final case class DockerComposeScaledService(name: String, numInstances: Int)

final case class DockerComposeServiceLogConsumer(serviceName: String, consumer: Consumer[OutputFrame])

object DockerComposeContainer {
  val ID_LENGTH = 6

  type OTCContainer = OTCDockerComposeContainer[T] forSome {type T <: OTCDockerComposeContainer[T]}

  private[DockerComposeContainer] def toExposedService(service: (String, Int)) = DockerComposeExposedService(service._1, service._2)

  //for backward compatibility with version <=1.7.3
  def apply(file: File, exposedService: Map[String, Int], identifier: String): DockerComposeContainer =
    DockerComposeContainer(Seq(file), exposedService.map(toExposedService).toSeq, identifier)

  //for backward compatibility with version <=1.7.3
  def apply(file: File, exposedService: Map[String, Int]): DockerComposeContainer =
    DockerComposeContainer(Seq(file), exposedService.map(toExposedService).toSeq)

  //for backward compatibility with version <=1.7.3
  def apply(file: File): DockerComposeContainer =
    DockerComposeContainer(Seq(file))

  protected[testcontainers] def randomIdentifier() = Base58.randomString(DockerComposeContainer.ID_LENGTH).toLowerCase()
}

final case class DockerComposeContainer(
                                         composeFiles: Seq[File],
                                         exposedServices: Seq[DockerComposeExposedService] = Seq.empty,
                                         identifier: String = DockerComposeContainer.randomIdentifier,
                                         scaledServices: Seq[DockerComposeScaledService] = Seq.empty,
                                         pull: Boolean = true,
                                         localCompose: Boolean = true,
                                         env: Map[String, String] = Map.empty,
                                         tailChildContainers: Boolean = false,
                                         logConsumers: Seq[DockerComposeServiceLogConsumer] = Seq.empty
                                       )
  extends TestContainerProxy[OTCDockerComposeContainer[_]] {

  private def init(): DockerComposeContainer.OTCContainer = {
    val container: DockerComposeContainer.OTCContainer = new OTCDockerComposeContainer(identifier, composeFiles.asJava)
    exposedServices.foreach { service =>
      if (service.instance.isDefined) {
        container.withExposedService(service.name, service.instance.get, service.port, service.waitStrategy)
      } else {
        container.withExposedService(service.name, service.port, service.waitStrategy)
      }
    }
    scaledServices.foreach { service =>
      container.withScaledService(service.name, service.numInstances)
    }
    container.withPull(pull)
    container.withLocalCompose(localCompose)
    container.withEnv(env.asJava)
    container.withTailChildContainers(tailChildContainers)
    logConsumers.foreach { serviceLogConsumer =>
      container.withLogConsumer(serviceLogConsumer.serviceName, serviceLogConsumer.consumer)
    }
    container
  }

  override val container: DockerComposeContainer.OTCContainer = init()

  def getServiceHost(serviceName: String, servicePort: Int): String = container.getServiceHost(serviceName, servicePort)

  def getServicePort(serviceName: String, servicePort: Int): Int = container.getServicePort(serviceName, servicePort)

}

trait TestContainerProxy[T <: FailureDetectingExternalResource] extends Container {

  @deprecated("Please use reflective methods from the wrapper and `configure` method for creation")
  implicit val container: T

  override def finished()(implicit description: Description): Unit = TestContainerAccessor.finished(description)

  override def succeeded()(implicit description: Description): Unit = TestContainerAccessor.succeeded(description)

  override def starting()(implicit description: Description): Unit = TestContainerAccessor.starting(description)

  override def failed(e: Throwable)(implicit description: Description): Unit = TestContainerAccessor.failed(e, description)
}

abstract class SingleContainer[T <: OTCGenericContainer[_]] extends TestContainerProxy[T] {

  def binds: Seq[Bind] = container.getBinds.asScala

  def command: Seq[String] = container.getCommandParts

  def containerId: String = container.getContainerId

  def containerInfo: InspectContainerResponse = container.getContainerInfo

  def containerIpAddress: String = container.getContainerIpAddress

  def containerName: String = container.getContainerName

  def env: Seq[String] = container.getEnv.asScala

  def exposedPorts: Seq[Int] = container.getExposedPorts.asScala.map(_.intValue())

  def extraHosts: Seq[String] = container.getExtraHosts.asScala

  import scala.concurrent.ExecutionContext.Implicits.global

  def image: Future[String] = Future {
    blocking {
      container.getImage.get()
    }
  }

  @deprecated("See org.testcontainers.containers.Network")
  def linkedContainers: Map[String, LinkableContainer] = container.getLinkedContainers.asScala.toMap

  def mappedPort(port: Int): Int = container.getMappedPort(port)

  def portBindings: Seq[String] = container.getPortBindings.asScala

  def networkMode: String = container.getNetworkMode

  def network: Network = container.getNetwork

  def networkAliases: Seq[String] = container.getNetworkAliases.asScala

  def privilegedMode: Boolean = container.isPrivilegedMode

  def volumesFroms: Seq[VolumesFrom] = container.getVolumesFroms.asScala

  def startupCheckStrategy: StartupCheckStrategy = container.getStartupCheckStrategy

  def startupAttempts: Int = container.getStartupAttempts

  def workingDirectory: String = container.getWorkingDirectory

  def dockerClient: DockerClient = container.getDockerClient

  def dockerDaemonInfo: Info = container.getDockerDaemonInfo

  def logConsumers: Seq[Consumer[OutputFrame]] = container.getLogConsumers.asScala

  def createContainerCmdModifiers: Set[Consumer[CreateContainerCmd]] = container.getCreateContainerCmdModifiers.asScala.toSet

  def configure(configProvider: T => Unit): this.type = {
    configProvider(container)
    this
  }
}
