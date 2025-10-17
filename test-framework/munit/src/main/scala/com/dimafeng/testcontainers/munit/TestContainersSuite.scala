package com.dimafeng.testcontainers.munit

import com.dimafeng.testcontainers.implicits.DockerImageNameConverters
import com.dimafeng.testcontainers.lifecycle.{Andable, TestLifecycleAware}
import com.dimafeng.testcontainers.munit.TestContainersSuite.IllegalWithContainersCall
import munit.Suite
import org.testcontainers.lifecycle.TestDescription

trait TestContainersSuite extends DockerImageNameConverters { self: Suite =>
  /**
   * To use testcontainers suites you need to declare,
   * which containers you want to use inside your tests.
   *
   * For example:
   * {{{
   *   override type Containers = MySQLContainer
   * }}}
   *
   * If you want to use multiple containers inside your tests, use `and` syntax:
   * {{{
   *   override type Containers = MySQLContainer and PostgreSQLContainer
   * }}}
   */
  type Containers <: Andable

  /**
   * Contains containers startup logic.
   * In this method you can use any intermediate logic.
   * You can pass parameters between containers, for example:
   * {{{
   * override def startContainers(): Containers = {
   *   val container1 = Container1.Def().start()
   *   val container2 = Container2.Def(container1.someParam).start()
   *   container1 and container2
   * }
   * }}}
   *
   * @return Started containers
   */
  def startContainers(): Containers

  /**
   * To use containers inside your test bodies you need to use `withContainers` function:
   * {{{
   * test("test") {
   *   withContainers { mysqlContainer =>
   *     // Inside your test body you can do with your container whatever you want to
   *     assert(mysqlContainer.jdbcUrl.nonEmpty)
   *   }
   * }
   * }}}
   *
   * `withContainers` also supports multiple containers:
   * {{{
   * test("test") {
   *   withContainers { case mysqlContainer and pgContainer =>
   *     // test body
   *   }
   * }
   * }}}
   *
   * @param runTest Test body
   */
  def withContainers[A](runTest: Containers => A): A = {
    val c = startedContainers.getOrElse(throw IllegalWithContainersCall())
    runTest(c)
  }

  /**
   * Override, if you want to do something after containers start.
   */
  def afterContainersStart(containers: Containers): Unit = {}

  /**
   * Override, if you want to do something before containers stop.
   */
  def beforeContainersStop(containers: Containers): Unit = {}

  @volatile private[testcontainers] var startedContainers: Option[Containers] = None

  val suiteDescription = TestContainersSuite.createDescription(self)

  private[testcontainers] def beforeTest(containers: Containers): Unit = {
    containers.foreach {
      case container: TestLifecycleAware => container.beforeTest(suiteDescription)
      case _ => // do nothing
    }
  }

  private[testcontainers] def afterTest(containers: Containers, throwable: Option[Throwable]): Unit = {
    containers.foreach {
      case container: TestLifecycleAware => container.afterTest(suiteDescription, throwable)
      case _ => // do nothing
    }
  }

  private[testcontainers] def stopContainers(containers: Containers): Unit = {
    try {
      beforeContainersStop(containers)
    }
    finally {
      try {
        startedContainers.foreach(_.stop())
      }
      finally {
        startedContainers = None
      }
    }
  }
}

object TestContainersSuite {
  case class IllegalWithContainersCall() extends IllegalStateException(
    "'withContainers' method can't be used before all containers are started. " +
      "'withContainers' method should be used only in test cases to prevent this."
  )

  def createDescription(suite: Suite): TestDescription = {
    new TestDescription {
      override def getTestId: String = suite.getClass.getName
      override def getFilesystemFriendlyName: String = suite.getClass.getName
    }
  }
}