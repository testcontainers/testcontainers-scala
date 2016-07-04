package com.dimafeng.testcontainers

import java.io.File
import org.junit.runner.Description
import org.scalatest._
import org.testcontainers.containers.{
GenericContainer, TestContainerAccessor, DockerComposeContainer => OTCDockerComposeContainer, MySQLContainer => OTCMySQLContainer
}

trait ForEachTestContainer extends SuiteMixin {
  self: Suite =>

  val container: Container

  implicit private val suiteDescription = Description.createSuiteDescription(self.getClass)

  abstract protected override def runTest(testName: String, args: Args): Status = {
    container.starting()
    try {
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
      container.finished()
    }
  }
}

trait ForAllTestContainer extends SuiteMixin {
  self: Suite =>

  val container: Container

  implicit private val suiteDescription = Description.createSuiteDescription(self.getClass)

  abstract override def run(testName: Option[String], args: Args): Status = {
    container.starting()
    try {
      super.run(testName, args)
    } finally {
      container.finished()
    }
  }
}

sealed trait Container {
  def finished()(implicit description: Description): Unit

  def failed(e: Throwable)(implicit description: Description): Unit

  def starting()(implicit description: Description): Unit

  def succeeded()(implicit description: Description): Unit
}

class DockerComposeContainer(composeFile: File, exposedService: Map[String, Int] = Map()) extends SingleContainer[OTCDockerComposeContainer[_]] {

  private val c = new OTCDockerComposeContainer(composeFile)
  exposedService.foreach { v => c.withExposedService(v._1, v._2); Unit }

  override def container = c

  def getServiceHost = c.getServiceHost _

  def getServicePort = c.getServicePort _
}

object DockerComposeContainer {
  def apply(composeFile: File, exposedService: Map[String, Int] = Map()) = new DockerComposeContainer(composeFile, exposedService)
}

abstract class SingleContainer[T <: GenericContainer[_]] extends Container {
  implicit def container: T

  override def finished()(implicit description: Description): Unit = TestContainerAccessor.finished(description)

  override def succeeded()(implicit description: Description): Unit = TestContainerAccessor.succeeded(description)

  override def starting()(implicit description: Description): Unit = TestContainerAccessor.starting(description)

  override def failed(e: Throwable)(implicit description: Description): Unit = TestContainerAccessor.failed(e, description)
}

class MultipleContainers[T <: Product] private(val _containers: T) extends Container {

  private def containersAsIterator = containers.productIterator.map(_.asInstanceOf[Container])

  def containers = _containers

  override def finished()(implicit description: Description): Unit = containersAsIterator.foreach(_.finished()(description))

  override def succeeded()(implicit description: Description): Unit = containersAsIterator.foreach(_.succeeded()(description))

  override def starting()(implicit description: Description): Unit = containersAsIterator.foreach(_.starting()(description))

  override def failed(e: Throwable)(implicit description: Description): Unit = containersAsIterator.foreach(_.failed(e)(description))
}

object MultipleContainers {
  def apply[T <: Container](t: T) =
    new MultipleContainers(new Tuple1(t))

  def apply[T1 <: Container, T2 <: Container](t1: T1, t2: T2) =
    new MultipleContainers((t1, t2))

  def apply[T1 <: Container, T2 <: Container, T3 <: Container](t1: T1, t2: T2, t3: T3) =
    new MultipleContainers((t1, t2, t3))

  def apply[T1 <: Container, T2 <: Container, T3 <: Container, T4 <: Container](t1: T1, t2: T2, t3: T3, t4: T4) =
    new MultipleContainers((t1, t2, t3, t4))

  def apply[T1 <: Container, T2 <: Container, T3 <: Container, T4 <: Container, T5 <: Container](t1: T1, t2: T2, t3: T3, t4: T4, t5: T5) =
    new MultipleContainers((t1, t2, t3, t4, t5))
}
