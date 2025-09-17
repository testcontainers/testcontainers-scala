package com.dimafeng.testcontainers.specs2

import com.dimafeng.testcontainers.implicits.DockerImageNameConverters
import com.dimafeng.testcontainers.lifecycle.{Andable, TestLifecycleAware}
import org.specs2.specification.core.SpecificationStructure
import org.junit.runner.{Description => JunitDescription}
import org.testcontainers.lifecycle.TestDescription

trait TestContainersSuite extends DockerImageNameConverters { self: SpecificationStructure =>
  type Containers <: Andable

  def startContainers(): Containers

  def withContainers[A](runTest: Containers => A): A = {
    val c = startedContainers.getOrElse(throw new IllegalStateException(
      "'withContainers' method can't be used before all containers are started. " +
        "'withContainers' method should be used only in test cases to prevent this."
    ))
    runTest(c)
  }

  def afterContainersStart(containers: Containers): Unit = {}
  def beforeContainersStop(containers: Containers): Unit = {}

  @volatile private[testcontainers] var startedContainers: Option[Containers] = None

  private[testcontainers] val suiteDescription: TestDescription = {
    val description = JunitDescription.createSuiteDescription(self.getClass)
    new TestDescription {
      override def getTestId: String = description.getDisplayName
      override def getFilesystemFriendlyName: String = s"${description.getClassName}-${description.getMethodName}"
    }
  }

  private[testcontainers] def beforeTest(containers: Containers): Unit = {
    containers.foreach {
      case container: TestLifecycleAware => container.beforeTest(suiteDescription)
      case _ => // do nothing
    }
  }

  private[testcontainers] def afterTest(containers: Containers, throwable: Option[Throwable]): Unit = {
    containers.foreach {
      case container: TestLifecycleAware => container.afterTest(suiteDescription, throwable)
      case _ => // do nothing
    }
  }

  private[testcontainers] def stopContainers(containers: Containers): Unit = {
    try {
      beforeContainersStop(containers)
    } finally {
      try {
        startedContainers.foreach(_.stop())
      } finally {
        startedContainers = None
      }
    }
  }
}