package org.testcontainers.testcontainers4s.containers.scalatest

import org.scalatest.{Args, CompositeStatus, Status, Suite, SuiteMixin}
import org.testcontainers.testcontainers4s.containers.ContainerDefList

trait ForAllTestContainer[C <: ContainerDefList] extends SuiteMixin { self: Suite =>

  def startContainers: C#Containers

  def withContainers(runTest: C#Containers => Unit): Unit = {
    val c = startedContainers.getOrElse(throw new IllegalStateException(
      "'withContainers' method can't be used before all containers are started. " +
        "'withContainers' method should be used only in test cases to prevent this."
    ))
    runTest(c)
  }

  @volatile private var startedContainers: Option[C#Containers] = None

  abstract override def run(testName: Option[String], args: Args): Status = {
    if (expectedTestCount(args.filter) == 0) {
      new CompositeStatus(Set.empty)
    } else {
      startedContainers = Some(startContainers)
      try {
        afterStart()
        super.run(testName, args)
      } finally {
        try {
          beforeStop()
        }
        finally {
          startedContainers.foreach(_.stop())
        }
      }
    }
  }

  def afterStart(): Unit = {}

  def beforeStop(): Unit = {}
}
