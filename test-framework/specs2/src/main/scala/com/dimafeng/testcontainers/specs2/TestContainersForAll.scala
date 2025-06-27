package com.dimafeng.testcontainers.specs2

import com.dimafeng.testcontainers.lifecycle.Andable
import org.specs2.execute.{AsResult, Result}
import org.specs2.specification.core.SpecificationStructure
import org.specs2.specification.{AroundEach, BeforeAfterAll}

trait TestContainersForAll extends TestContainersSuite with BeforeAfterAll with AroundEach { self: SpecificationStructure =>

  override def beforeAll(): Unit = {
    val containers = startContainers()
    startedContainers = Some(containers)
    try {
      afterContainersStart(containers)
    } catch {
      case e: Throwable =>
        stopContainers(containers)
        throw e
    }
  }

  override def around[R: AsResult](r: => R): Result = {
    startedContainers.foreach(beforeTest)
    val result = AsResult(r)
    val throwable = result match {
      case f: org.specs2.execute.Failure => Some(f.exception)
      case e: org.specs2.execute.Error   => Some(e.exception)
      case _                             => None
    }
    startedContainers.foreach(afterTest(_, throwable))
    result
  }

  override def afterAll(): Unit = {
    startedContainers.foreach(stopContainers)
  }
}