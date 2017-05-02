package com.dimafeng.testcontainers

import java.io.File
import org.junit.runner.Description
import org.scalatest._
import org.testcontainers.containers.traits.LinkableContainer
import org.testcontainers.containers.{GenericContainer => OTCGenericContainer, DockerComposeContainer => OTCDockerComposeContainer, MySQLContainer => OTCMySQLContainer, FailureDetectingExternalResource, BrowserWebDriverContainer, TestContainerAccessor}
import org.testcontainers.shaded.com.github.dockerjava.api.command.InspectContainerResponse
import org.testcontainers.shaded.com.github.dockerjava.api.model.Bind
import scala.collection.JavaConverters._
import scala.concurrent.Future

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
    container.starting()
    afterStart()
    try {
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

  def afterStart(): Unit = {}

  def beforeStop(): Unit = {}
}

sealed trait Container {
  def finished()(implicit description: Description): Unit

  def failed(e: Throwable)(implicit description: Description): Unit

  def starting()(implicit description: Description): Unit

  def succeeded()(implicit description: Description): Unit
}

class DockerComposeContainer(composeFile: File, exposedService: Map[String, Int] = Map()) extends TestContainerProxy[OTCDockerComposeContainer[_]] {

  type OTCContainer = OTCDockerComposeContainer[T] forSome {type T <: OTCDockerComposeContainer[T]}
  override val container: OTCContainer = new OTCDockerComposeContainer(composeFile)
  exposedService.foreach(Function.tupled(container.withExposedService))

  def getServiceHost = container.getServiceHost _

  def getServicePort = container.getServicePort _
}

object DockerComposeContainer {
  def apply(composeFile: File, exposedService: Map[String, Int] = Map()) = new DockerComposeContainer(composeFile, exposedService)
}

trait TestContainerProxy[T <: FailureDetectingExternalResource] extends Container {
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
    container.getImage.get()
  }

  def linkedContainers: Map[String, LinkableContainer] = container.getLinkedContainers.asScala.toMap

  def mappedPort(port: Int): Int = container.getMappedPort(port)

  def portBindings: Seq[String] = container.getPortBindings.asScala
}

class MultipleContainers[T <: Product] private(val _containers: T) extends Container {

  private def containersAsIterator = containers.productIterator.map(_.asInstanceOf[Container])

  def containers: T = _containers

  override def finished()(implicit description: Description): Unit = containersAsIterator.foreach(_.finished()(description))

  override def succeeded()(implicit description: Description): Unit = containersAsIterator.foreach(_.succeeded()(description))

  override def starting()(implicit description: Description): Unit = containersAsIterator.foreach(_.starting()(description))

  override def failed(e: Throwable)(implicit description: Description): Unit = containersAsIterator.foreach(_.failed(e)(description))
}

object MultipleContainers {
  def apply[T <: Container](t: T) =
    new MultipleContainers(Tuple1(t))

  def apply[T1 <: Container, T2 <: Container](t1: T1, t2: T2) =
    new MultipleContainers((t1, t2))

  def apply[T1 <: Container, T2 <: Container, T3 <: Container](t1: T1, t2: T2, t3: T3) =
    new MultipleContainers((t1, t2, t3))

  def apply[T1 <: Container, T2 <: Container, T3 <: Container, T4 <: Container](t1: T1, t2: T2, t3: T3, t4: T4) =
    new MultipleContainers((t1, t2, t3, t4))

  def apply[T1 <: Container, T2 <: Container, T3 <: Container, T4 <: Container, T5 <: Container](t1: T1, t2: T2, t3: T3, t4: T4, t5: T5) =
    new MultipleContainers((t1, t2, t3, t4, t5))
}