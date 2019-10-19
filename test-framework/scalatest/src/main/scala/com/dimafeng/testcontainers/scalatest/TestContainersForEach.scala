package com.dimafeng.testcontainers.scalatest

import org.scalatest.{Args, Status, Suite}

/**
  * Starts containers before each test and stop them after each test
  */
trait TestContainersForEach extends TestContainersSuite { self: Suite =>

  abstract protected override def runTest(testName: String, args: Args): Status = {
    val containers = startContainers()
    startedContainers = Some(containers)

    @volatile var testCalled = false
    @volatile var afterTestCalled = false

    try {
      afterStart()
      beforeTest(containers)

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
          afterTest(containers, Some(e))
        }

        throw e
    }
    finally {
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
