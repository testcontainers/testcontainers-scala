package com.dimafeng.testcontainers

import com.dimafeng.testcontainers.lifecycle.TestLifecycleAware
import org.junit.runner.Description
import org.testcontainers.lifecycle.TestDescription

import scala.language.implicitConversions

class MultipleContainers private(containers: Seq[LazyContainer[_]]) extends Container with TestLifecycleAware {

  @deprecated("Use `stop` instead", "v0.27.0")
  override def finished()(implicit description: Description): Unit = containers.foreach(_.finished()(description))

  @deprecated("Use `stop` and/or `TestLifecycleAware.afterTest` instead", "v0.27.0")
  override def succeeded()(implicit description: Description): Unit = containers.foreach(_.succeeded()(description))

  @deprecated("Use `start` instead", "v0.27.0")
  override def starting()(implicit description: Description): Unit = containers.foreach(_.starting()(description))

  @deprecated("Use `stop` and/or `TestLifecycleAware.afterTest` instead", "v0.27.0")
  override def failed(e: Throwable)(implicit description: Description): Unit = containers.foreach(_.failed(e)(description))

  override def beforeTest(description: TestDescription): Unit = {
    containers.foreach(_.beforeTest(description))
  }

  override def afterTest(description: TestDescription, throwable: Option[Throwable]): Unit = {
    containers.foreach(_.afterTest(description, throwable))
  }

  override def start(): Unit = containers.foreach(_.start())

  override def stop(): Unit = containers.foreach(_.stop())
}

object MultipleContainers {

  /**
    * Creates a `MultipleContainers` instance with nested containers (support 2+ nested containers)
    *  {{{
    *  val pgContainer = PostgreSQLContainer()
    *  val mySqlContainer = MySQLContainer()
    *  val seleniumContainer = SeleniumContainer()
    *
    *  val containers = MultipleContainers(pgContainer, mySqlContainer, seleniumContainer)
    *  }}}
    *
    * In case of dependent containers you need to define this containers explicitly with `lazy val`,
    * and after that pass them to the `MultipleContainers`:
    *  {{{
    *  lazy val pgContainer = PostgreSQLContainer()
    *  lazy val appContainer = AppContainer(pgContainer.jdbcUrl, pgContainer.username, pgContainer.password)
    *
    *  val containers = MultipleContainers(pgContainer, appContainer)
    *  }}}
    */
  def apply(containers: LazyContainer[_]*): MultipleContainers = new MultipleContainers(containers)
}

/**
  * Lazy container wrapper aims to solve the problem of cross-container dependencies in `MultipleContainers` when a second container
  * requires some after start data from a first one (e.g. an application container needs JDBC url of a container with a database - in that case
  * the url becomes available after the database container has started)
  *
  * You don't need to wrap your containers into the `LazyContainer` manually
  * when you pass your containers in the `MultipleContainers`- there is implicit conversion for that.
  */
class LazyContainer[T <: Container](factory: => T) extends Container with TestLifecycleAware {
  lazy val container: T = factory

  @deprecated("Use `stop` instead", "v0.27.0")
  override def finished()(implicit description: Description): Unit = container.finished()

  @deprecated("Use `stop` and/or `TestLifecycleAware.afterTest` instead", "v0.27.0")
  override def failed(e: Throwable)(implicit description: Description): Unit = container.failed(e)

  @deprecated("Use `start` instead", "v0.27.0")
  override def starting()(implicit description: Description): Unit = container.starting()

  @deprecated("Use `stop` and/or `TestLifecycleAware.afterTest` instead", "v0.27.0")
  override def succeeded()(implicit description: Description): Unit = container.succeeded()

  override def beforeTest(description: TestDescription): Unit = {
    container match {
      case c: TestLifecycleAware => c.beforeTest(description)
      case _ => // do nothing
    }
  }

  override def afterTest(description: TestDescription, throwable: Option[Throwable]): Unit = {
    container match {
      case c: TestLifecycleAware => c.afterTest(description, throwable)
      case _ => // do nothing
    }
  }

  override def start(): Unit = container.start()

  override def stop(): Unit = container.stop()
}

object LazyContainer {

  implicit def containerToLazyContainer[T <: Container](container: => T): LazyContainer[T] = LazyContainer(container)

  def apply[T <: Container](factory: => T): LazyContainer[T] = new LazyContainer(factory)
}
