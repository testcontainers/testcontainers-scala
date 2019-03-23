package org.testcontainers.testcontainers4s.lifecycle

import org.testcontainers.lifecycle.TestDescription
import org.testcontainers.testcontainers4s.containers.Container

// TODO: this stuff should be done by test framework, not by testcontainers
trait TestLifecycleAware { self: Container[_] =>

  def beforeTest(description: TestDescription): Unit = {}

  def afterTest(description: TestDescription, throwable: Option[Throwable]): Unit = {}
}
