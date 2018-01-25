package com.dimafeng.testcontainers

import java.io.File

import com.github.dockerjava.api.command.InspectContainerResponse
import com.github.dockerjava.api.model.Bind
import org.junit.runner.Description
import org.scalatest._
import org.testcontainers.containers.traits.LinkableContainer
import org.testcontainers.containers.{FailureDetectingExternalResource, TestContainerAccessor, DockerComposeContainer => OTCDockerComposeContainer, GenericContainer => OTCGenericContainer}
import org.testcontainers.utility.Base58

import scala.collection.JavaConverters._
import scala.concurrent.Future

trait ForEachTestContainer extends SuiteMixin {
  self: Suite =>

  implicit private val suiteDescription = Description.createSuiteDescription(self.getClass)

  implicit val testContainersContext: TestContainersContext = new TestContainersContext.Default

  abstract protected override def runTest(testName: String, args: Args): Status = {
    testContainersContext.startAll()
    try {
      afterStart()
      val status = super.runTest(testName, args)
      status match {
        case FailedStatus => testContainersContext.failAll(new RuntimeException(status.toString))
        case _ => testContainersContext.succeedAll()
      }
      status
    }
    catch {
      case e: Throwable =>
        testContainersContext.failAll(e)
        throw e
    }
    finally {
      try {
        beforeStop()
      }
      finally {
        testContainersContext.finishAll()
      }
    }
  }

  def afterStart(): Unit = {}

  def beforeStop(): Unit = {}
}

trait ForAllTestContainer extends SuiteMixin {
  self: Suite =>

  implicit private val suiteDescription = Description.createSuiteDescription(self.getClass)

  implicit val testContainersContext: TestContainersContext = new TestContainersContext.Default

  abstract override def run(testName: Option[String], args: Args): Status = {
    if (expectedTestCount(args.filter) == 0) {
      new CompositeStatus(Set.empty)
    } else {
      testContainersContext.startAll()
      try {
        afterStart()
        super.run(testName, args)
      } finally {
        try {
          beforeStop()
        }
        finally {
          testContainersContext.finishAll()
        }
      }
    }
  }

  def afterStart(): Unit = {}

  def beforeStop(): Unit = {}
}

abstract class Container()(implicit testContainersContext: TestContainersContext) {

  def finished()(implicit description: Description): Unit

  def failed(e: Throwable)(implicit description: Description): Unit

  def starting()(implicit description: Description): Unit

  def succeeded()(implicit description: Description): Unit

  testContainersContext.add(this)
}

class DockerComposeContainer(
  composeFiles: Seq[File], exposedService: Map[String, Int] = Map(), identifier: String
)(implicit testContainersContext: TestContainersContext) extends TestContainerProxy[OTCDockerComposeContainer[_]]() {

  type OTCContainer = OTCDockerComposeContainer[T] forSome {type T <: OTCDockerComposeContainer[T]}
  override val container: OTCContainer = new OTCDockerComposeContainer(identifier, composeFiles.asJava)
  exposedService.foreach(Function.tupled(container.withExposedService))

  def getServiceHost = container.getServiceHost _

  def getServicePort = container.getServicePort _
}

object DockerComposeContainer {
  def apply(composeFiles: Seq[File],
            exposedService: Map[String, Int],
            identifier: String)(implicit testContainersContext: TestContainersContext): DockerComposeContainer =
    new DockerComposeContainer(composeFiles, exposedService, identifier)

  def apply(
    composeFile: File,
    exposedService: Map[String, Int] = Map()
  )(implicit testContainersContext: TestContainersContext): DockerComposeContainer =
    apply(Seq(composeFile), exposedService, Base58.randomString(6).toLowerCase())
}

abstract class TestContainerProxy[T <: FailureDetectingExternalResource]()(
  implicit testContainersContext: TestContainersContext
) extends Container() {

  implicit val container: T

  override def finished()(implicit description: Description): Unit = TestContainerAccessor.finished(description)

  override def succeeded()(implicit description: Description): Unit = TestContainerAccessor.succeeded(description)

  override def starting()(implicit description: Description): Unit = TestContainerAccessor.starting(description)

  override def failed(e: Throwable)(implicit description: Description): Unit = TestContainerAccessor.failed(e, description)
}

abstract class SingleContainer[T <: OTCGenericContainer[_]]()(
  implicit testContainersContext: TestContainersContext
) extends TestContainerProxy[T]() {

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
