package org.testcontainers.containers

import org.junit.runner.Description

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
