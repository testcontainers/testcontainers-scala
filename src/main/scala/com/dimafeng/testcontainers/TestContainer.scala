package com.dimafeng.testcontainers

import org.junit.runner.Description
import org.scalatest._
import org.testcontainers.containers.{GenericContainer, TestContainerAccessor}

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

object Container {
  def apply(genericContainers: GenericContainer[_]*): Container = {
    if (genericContainers.length == 1) {
      new SingleContainer(genericContainers.head)
    } else {
      new MultipleContainers(genericContainers.toSeq.map(apply(_)))
    }
  }
}

class SingleContainer(genericContainer: GenericContainer[_]) extends Container {
  implicit val _genericContainer = genericContainer

  override def finished()(implicit description: Description): Unit = TestContainerAccessor.finished(description)

  override def succeeded()(implicit description: Description): Unit = TestContainerAccessor.succeeded(description)

  override def starting()(implicit description: Description): Unit = TestContainerAccessor.starting(description)

  override def failed(e: Throwable)(implicit description: Description): Unit = TestContainerAccessor.failed(e, description)
}

class MultipleContainers(containers: Seq[Container]) extends Container {
  override def finished()(implicit description: Description): Unit = containers.foreach(_.finished()(description))

  override def succeeded()(implicit description: Description): Unit = containers.foreach(_.succeeded()(description))

  override def starting()(implicit description: Description): Unit = containers.foreach(_.starting()(description))

  override def failed(e: Throwable)(implicit description: Description): Unit = containers.foreach(_.failed(e)(description))
}