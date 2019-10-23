package com.dimafeng.testcontainers

import com.dimafeng.testcontainers.TestContainers.TestContainersSuite
import org.junit.runner.Description
import org.scalatest._

/**
  * Starts a container for the first test in the suite and stops after the last test in the suite
  */
trait ForAllTestContainer extends TestContainersSuite {
  self: Suite =>

  abstract override def run(testName: Option[String], args: Args): Status = {
    if (expectedTestCount(args.filter) == 0) {
      new CompositeStatus(Set.empty)
    } else {
      container.start()
      try {
        afterStart()
        super.run(testName, args)
      } finally {
        try {
          beforeStop()
        }
        finally {
          container.stop()
        }
      }
    }
  }

  abstract protected override def runTest(testName: String, args: Args): Status = {
    @volatile var testCalled = false
    @volatile var afterTestCalled = false

    try {
      beforeTest()

      testCalled = true
      val status = super.runTest(testName, args)

      afterTestCalled = true
      if (!status.succeeds()) {
        afterTest(Some(new RuntimeException("Test failed")))
      } else {
        afterTest(None)
      }

      status
    }
    catch {
      case e: Throwable =>
        if (testCalled && !afterTestCalled) {
          afterTestCalled = true
          afterTest(Some(e))
        }

        throw e
    }
  }
}

