package com.dimafeng.testcontainers.scalatest

import com.dimafeng.testcontainers.TestContainers
import com.dimafeng.testcontainers.lifecycle.{Andable, TestLifecycleAware}
import org.scalatest.{Suite, SuiteMixin}

private[scalatest] trait TestContainersSuite extends SuiteMixin { self: Suite =>

  import TestContainers._

  type Containers <: Andable

  def startContainers(): Containers

  def withContainers(runTest: Containers => Unit): Unit = {
    val c = startedContainers.getOrElse(throw IllegalWithContainersCall())
    runTest(c)
  }

  def afterStart(): Unit = {}

  def beforeStop(): Unit = {}

  @volatile private[testcontainers] var startedContainers: Option[Containers] = None

  private val suiteDescription = createDescription(self)

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
}

case class IllegalWithContainersCall() extends IllegalStateException(
  "'withContainers' method can't be used before all containers are started. " +
    "'withContainers' method should be used only in test cases to prevent this."
)
