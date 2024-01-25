package org.testcontainers.containers

import org.junit.runner.Description

@deprecated("Should be replaced by lifecycle methods", "v0.27.0")
object TestContainerAccessor {
  def finished[T <:FailureDetectingExternalResource](description: Description)(implicit container: T): Unit =
    container.finished(description)

  def failed[T <:FailureDetectingExternalResource](e: Throwable, description: Description)(implicit container: T): Unit =
    container.failed(e, description)

  def starting[T <:FailureDetectingExternalResource](description: Description)(implicit container: T): Unit =
    container.starting(description)

  def succeeded[T <:FailureDetectingExternalResource](description: Description)(implicit container: T): Unit =
    container.succeeded(description)
}
