package com.dimafeng.testcontainers

import com.dimafeng.testcontainers.TestContainers.TestContainersSuite
import org.scalatest._

/**
  * Starts and stops a container for each tests within the suite
  */
trait ForEachTestContainer extends TestContainersSuite {
  self: Suite =>

  abstract protected override def runTest(testName: String, args: Args): Status = {
    container.start()

    @volatile var testCalled = false
    @volatile var afterTestCalled = false

    try {
      afterStart()
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
    finally {
      try {
        beforeStop()
      }
      finally {
        container.stop()
      }
    }
  }
}
