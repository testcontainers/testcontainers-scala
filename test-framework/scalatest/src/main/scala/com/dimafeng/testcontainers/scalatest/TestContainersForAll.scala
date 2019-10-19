package com.dimafeng.testcontainers.scalatest

import org.scalatest.{Args, CompositeStatus, Status, Suite}

/**
  * Starts containers before all tests and stop then after all tests
  */
trait TestContainersForAll extends TestContainersSuite { self: Suite =>

  abstract override def run(testName: Option[String], args: Args): Status = {
    if (expectedTestCount(args.filter) == 0) {
      new CompositeStatus(Set.empty)
    } else {
      startedContainers = Some(startContainers())
      try {
        afterStart()
        super.run(testName, args)
      } finally {
        try {
          beforeStop()
        }
        finally {
          try {
            startedContainers.foreach(_.stop())
          }
          finally {
            startedContainers = None
          }
        }
      }
    }
  }

  abstract protected override def runTest(testName: String, args: Args): Status = {
    @volatile var testCalled = false
    @volatile var afterTestCalled = false

    try {
      startedContainers.foreach(beforeTest)

      testCalled = true
      val status = super.runTest(testName, args)

      afterTestCalled = true
      if (!status.succeeds()) {
        val err = new RuntimeException("Test failed")
        startedContainers.foreach(afterTest(_, Some(err)))
      } else {
        startedContainers.foreach(afterTest(_, None))
      }

      status
    }
    catch {
      case e: Throwable =>
        if (testCalled && !afterTestCalled) {
          afterTestCalled = true
          startedContainers.foreach(afterTest(_, Some(e)))
        }

        throw e
    }
  }
}
