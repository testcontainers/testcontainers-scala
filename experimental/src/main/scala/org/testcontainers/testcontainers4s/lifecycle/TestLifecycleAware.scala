package org.testcontainers.testcontainers4s.lifecycle

import org.testcontainers.lifecycle.TestDescription
import org.testcontainers.testcontainers4s.containers.Container

trait TestLifecycleAware { self: Container =>

  def beforeTest(description: TestDescription): Unit = {}

  def afterTest(description: TestDescription, throwable: Option[Throwable]): Unit = {}
}
