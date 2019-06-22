package com.dimafeng.testcontainers.lifecycle

import com.dimafeng.testcontainers.Container
import org.testcontainers.lifecycle.TestDescription

trait TestLifecycleAware { self: Container =>

  def beforeTest(description: TestDescription): Unit = {}

  def afterTest(description: TestDescription, throwable: Option[Throwable]): Unit = {}
}
