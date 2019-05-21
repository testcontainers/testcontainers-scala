package com.dimafeng.testcontainers

import org.junit.runner.Description
import org.scalatest._

/**
  * Starts and stops a container for each tests within the suite
  */
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
