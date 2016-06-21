package org.testcontainers.containers

import org.junit.runner.Description

object TestContainerAccessor {
  def finished(description: Description)(implicit container: GenericContainer[_]): Unit =
    container.finished(description)

  def failed(e: Throwable, description: Description)(implicit container: GenericContainer[_]): Unit =
    container.failed(e, description)

  def starting(description: Description)(implicit container: GenericContainer[_]): Unit =
    container.starting(description)

  def succeeded(description: Description)(implicit container: GenericContainer[_]): Unit =
    container.succeeded(description)
}
