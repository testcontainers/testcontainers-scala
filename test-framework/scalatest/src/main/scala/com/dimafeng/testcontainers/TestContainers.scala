package com.dimafeng.testcontainers

import com.dimafeng.testcontainers.implicits.DockerImageNameConverters
import com.dimafeng.testcontainers.lifecycle.TestLifecycleAware
import org.scalatest._
import org.testcontainers.lifecycle.TestDescription

private[testcontainers] object TestContainers {

  def createDescription(suite: Suite): TestDescription = {
    new TestDescription {
      override def getTestId: String = suite.getClass.getName
      override def getFilesystemFriendlyName: String = suite.getClass.getName
    }
  }

  trait TestContainersSuite extends SuiteMixin with DockerImageNameConverters { self: Suite =>

    def container: Container

    def afterStart(): Unit = {}

    def beforeStop(): Unit = {}

    private val suiteDescription: TestDescription = createDescription(self)

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
