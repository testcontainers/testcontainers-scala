package com.dimafeng.testcontainers

import org.junit.runner.Description
import org.scalatest._

/**
  * Starts a container for the first test in the suite and stops after the last test in the suite
  */
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

