package com.dimafeng.testcontainers

import com.dimafeng.testcontainers.lifecycle.TestLifecycleAware
import org.junit.runner.{Description => JunitDescription}
import org.scalatest._
import org.testcontainers.lifecycle.TestDescription

private[testcontainers] object TestContainers {

  implicit def junit2testContainersDescription(junit: JunitDescription): TestDescription = {
    new TestDescription {
      override def getTestId: String = junit.getDisplayName
      override def getFilesystemFriendlyName: String = s"${junit.getClassName}-${junit.getMethodName}"
    }
  }

  // Copy-pasted from `org.scalatest.junit.JUnitRunner.createDescription`
  def createDescription(suite: Suite): JunitDescription = {
    val description = JunitDescription.createSuiteDescription(suite.getClass)
    // If we don't add the testNames and nested suites in, we get
    // Unrooted Tests show up in Eclipse
    for (name <- suite.testNames) {
      description.addChild(JunitDescription.createTestDescription(suite.getClass, name))
    }
    for (nestedSuite <- suite.nestedSuites) {
      description.addChild(createDescription(nestedSuite))
    }
    description
  }

  trait TestContainersSuite extends SuiteMixin { self: Suite =>

    val container: Container

    def afterStart(): Unit = {}

    def beforeStop(): Unit = {}

    private val suiteDescription = createDescription(self)

    private[testcontainers] def beforeTest(): Unit = {
      container match {
        case container: TestLifecycleAware => container.beforeTest(suiteDescription)
        case _ => // do nothing
      }
    }

    private[testcontainers] def afterTest(throwable: Option[Throwable]): Unit = {
      container match {
        case container: TestLifecycleAware => container.afterTest(suiteDescription, throwable)
        case _ => // do nothing
      }
    }
  }
}
