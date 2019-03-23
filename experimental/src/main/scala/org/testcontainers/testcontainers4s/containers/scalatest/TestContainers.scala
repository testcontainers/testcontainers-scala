package org.testcontainers.testcontainers4s.containers.scalatest

import org.scalatest.{Args, CompositeStatus, Status, Suite, SuiteMixin}
import org.testcontainers.testcontainers4s.containers.ContainerDefList
import org.testcontainers.testcontainers4s.containers.scalatest.TestContainers.TestContainersSuite

private[scalatest] object TestContainers {

  trait TestContainersSuite[C <: ContainerDefList] extends SuiteMixin { self: Suite =>

    def startContainers(): C#Containers

    def withContainers(runTest: C#Containers => Unit): Unit = {
      val c = startedContainers.getOrElse(throw IllegalWithContainersCall())
      runTest(c)
    }

    @volatile private[scalatest] var startedContainers: Option[C#Containers] = None

    def afterStart(): Unit = {}

    def beforeStop(): Unit = {}
  }
}

case class IllegalWithContainersCall() extends IllegalStateException(
  "'withContainers' method can't be used before all containers are started. " +
    "'withContainers' method should be used only in test cases to prevent this."
)

trait TestContainersForAll[C <: ContainerDefList] extends TestContainersSuite[C] { self: Suite =>

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
        } finally {
          try {
            startedContainers.foreach(_.stop())
          } finally {
            startedContainers = None
          }
        }
      }
    }
  }
}

trait TestContainersForEach[C <: ContainerDefList] extends TestContainersSuite[C] { self: Suite =>

  abstract protected override def runTest(testName: String, args: Args): Status = {
    val containers = startContainers()
    startedContainers = Some(containers)

    try {
      afterStart()
      super.runTest(testName, args)
    } finally {
      try {
        beforeStop()
      } finally {
        try {
          containers.stop()
        } finally {
          startedContainers = None
        }
      }
    }
  }
}
