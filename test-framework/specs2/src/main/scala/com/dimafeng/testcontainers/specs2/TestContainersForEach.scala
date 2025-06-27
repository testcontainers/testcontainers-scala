package com.dimafeng.testcontainers.specs2

import com.dimafeng.testcontainers.lifecycle.Andable
import org.specs2.execute.{AsResult, Result}
import org.specs2.specification.core.SpecificationStructure
import org.specs2.specification.AroundEach

trait TestContainersForEach extends TestContainersSuite with AroundEach { self: SpecificationStructure =>

  override def around[R: AsResult](r: => R): Result = {
    val containers = startContainers()
    startedContainers = Some(containers)
    try {
      afterContainersStart(containers)
      beforeTest(containers)

      val result = AsResult(r)

      val throwable = result match {
        case f: org.specs2.execute.Failure => Some(f.exception)
        case e: org.specs2.execute.Error   => Some(e.exception)
        case _                             => None
      }
      afterTest(containers, throwable)
      result
    } finally {
      stopContainers(containers)
    }
  }
}