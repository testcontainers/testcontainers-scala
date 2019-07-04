package com.dimafeng.testcontainers

import org.testcontainers.lifecycle.TestDescription

trait TestLifecycleAware {

  def beforeTest(description: TestDescription): Unit = {}

  def afterTest(description: TestDescription, throwable: Option[Throwable]): Unit = {}
}
