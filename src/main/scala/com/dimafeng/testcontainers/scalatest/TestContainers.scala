package com.dimafeng.testcontainers.scalatest

import com.dimafeng.testcontainers.lifecycle.TestLifecycleAware
import com.dimafeng.testcontainers.{ContainerDef, ContainerList}
import com.dimafeng.testcontainers.scalatest.TestContainers.TestContainersSuite
import org.junit.runner.{Description => JunitDescription}
import org.scalatest.{Args, CompositeStatus, Status, Suite, SuiteMixin}
import org.testcontainers.lifecycle.TestDescription

private[scalatest] object TestContainers {

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

    type Containers <: ContainerList

    def startContainers(): Containers

    def withContainers(runTest: Containers => Unit): Unit = {
      val c = startedContainers.getOrElse(throw IllegalWithContainersCall())
      runTest(c)
    }

    private val suiteDescription = createDescription(self)

    @volatile private[scalatest] var startedContainers: Option[Containers] = None

    private[scalatest] def beforeTest(containers: Containers): Unit = {
      containers.foreach {
        case container: TestLifecycleAware => container.beforeTest(suiteDescription)
        case _ => // do nothing
      }
    }

    private[scalatest] def afterTest(containers: Containers, throwable: Option[Throwable]): Unit = {
      containers.foreach {
        case container: TestLifecycleAware => container.afterTest(suiteDescription, throwable)
        case _ => // do nothing
      }
    }

    def afterStart(): Unit = {}

    def beforeStop(): Unit = {}
  }
}

case class IllegalWithContainersCall() extends IllegalStateException(
  "'withContainers' method can't be used before all containers are started. " +
    "'withContainers' method should be used only in test cases to prevent this."
)

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
      if (!status.succeeds()) {
        afterTestCalled = true
        startedContainers.foreach(afterTest(_, Some(new RuntimeException(status.toString))))
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
      if (!status.succeeds()) {
        afterTestCalled = true
        afterTest(containers, Some(new RuntimeException(status.toString)))
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
          containers.stop()
        }
        finally {
          startedContainers = None
        }
      }
    }
  }
}

trait TestContainerForAll extends TestContainersForAll { self: Suite =>

  val containerDef: ContainerDef

  final override type Containers = containerDef.Container

  override def startContainers(): containerDef.Container = {
    containerDef.start()
  }
}

trait TestContainerForEach extends TestContainersForEach { self: Suite =>

  val containerDef: ContainerDef

  final override type Containers = containerDef.Container

  override def startContainers(): containerDef.Container = {
    containerDef.start()
  }
}
