package com.dimafeng.testcontainers

import org.junit.runner.Description
import shapeless._
import shapeless.ops.hlist._

class MultipleContainers[H <: HList] private(val containers: H)(implicit ev: ToTraversable.Aux[H, List, Any]) extends Container {

  private lazy val containersAsIterator = containers.toList.map(_.asInstanceOf[Container])

  override def finished()(implicit description: Description): Unit = containersAsIterator.foreach(_.finished()(description))

  override def succeeded()(implicit description: Description): Unit = containersAsIterator.foreach(_.succeeded()(description))

  override def starting()(implicit description: Description): Unit = containersAsIterator.foreach(_.starting()(description))

  override def failed(e: Throwable)(implicit description: Description): Unit = containersAsIterator.foreach(_.failed(e)(description))
}

object MultipleContainers {

  import shapeless.Generic

  /**
    * Creates a `MultipleContainers` instance with nested containers (support 2+ nested containers)
    *  {{{
    *  val containers = MultipleContainers(PostgreSQLContainer(), MySQLContainer(), SeleniumContainer())
    *  }}}
    *
    *  Allows container dependencies using `LazyContainer`
    *  {{{
    *      val pgContainer = PostgreSQLContainer()
    *      val appContainer = AppContainer(pgContainer.jdbcUrl, pgContainer.username, pgContainer.password)
    *
    *      val containers = MultipleContainers(LazyContainer(pgContainer), LazyContainer(appContainer))
    *  }}}
    */
  def apply[P <: Product, L <: HList](p: P)(implicit gen: Generic.Aux[P, L], ev: ToTraversable.Aux[L, List, Any]): MultipleContainers[L] =
    new MultipleContainers[L](gen.to(p))
}

/**
  * Lazy container wrapper aims to solve the problem of cross-container dependencies in `MultipleContainers` when a second container
  * requires some after start data from a first one (e.g. an application container needs JDBC url of a container with a database - in that case
  * the url becomes available after the database container has started)
  */
class LazyContainer[T <: Container](factory: => T) extends Container {
  lazy val container: T = factory

  override def finished()(implicit description: Description): Unit = container.finished

  override def failed(e: Throwable)(implicit description: Description): Unit = container.failed(e)

  override def starting()(implicit description: Description): Unit = container.starting()

  override def succeeded()(implicit description: Description): Unit = container.succeeded()
}

object LazyContainer {
  def apply[T <: Container](factory: => T): LazyContainer[T] = new LazyContainer(factory)
}
