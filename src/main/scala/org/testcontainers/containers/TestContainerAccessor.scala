package org.testcontainers.containers

import org.junit.runner.Description

object TestContainerAccessor {
  def finished[T <:GenericContainer[_]](description: Description)(implicit container: T): Unit =
    container.finished(description)

  def failed[T <:GenericContainer[_]](e: Throwable, description: Description)(implicit container: T): Unit =
    container.failed(e, description)

  def starting[T <:GenericContainer[_]](description: Description)(implicit container: T): Unit =
    container.starting(description)

  def succeeded[T <:GenericContainer[_]](description: Description)(implicit container: T): Unit =
    container.succeeded(description)
}
