package com.dimafeng.testcontainers

import com.dimafeng.testcontainers.lifecycle.Andable
import munit.Suite

/**
 * Starts containers before each test and stop them after each test
 *
 * Example:
 * {{{
 * class ExampleSpec extends FunSuite with TestContainersForEach {
 *
 *   // First of all, you need to declare, which containers you want to use
 *   override type Containers = MySQLContainer and PostgreSQLContainer
 *
 *   // After that, you need to describe, how you want to start them,
 *   // In this method you can use any intermediate logic.
 *   // You can pass parameters between containers, for example.
 *   override def startContainers(): Containers = {
 *     val container1 = MySQLContainer.Def().start()
 *     val container2 = PostgreSQLContainer.Def().start()
 *     container1 and container2
 *   }
 *
 *   // `withContainers` function supports multiple containers:
 *   test("test") {
 *     withContainers { case mysqlContainer and pgContainer =>
 *       // Inside your test body you can do with your containers whatever you want to
 *       assert(mysqlContainer.jdbcUrl.nonEmpty && pgContainer.jdbcUrl.nonEmpty)
 *     }
 *   }
 * }
 *
 * Notes:
 * - If you override beforeEach() without calling super.beforeEach() your containers won't start
 * - If you override afterEach() without calling super.afterEach() your containers won't stop
 * }}}
 */
trait TestContainersForEach extends TestContainersSuite { self: Suite =>
  type Containers <: Andable

  override def beforeEach(context: BeforeEach): Unit = {
    val containers = startContainers()
    startedContainers = Some(containers)
    try {
      afterContainersStart(containers)
      beforeTest(containers)
    } catch {
      case e: Throwable =>
        stopContainers(containers)
        throw e
    }
  }

  override def afterEach(context: AfterEach): Unit = {
    startedContainers.foreach(afterTest(_, None)) // TODO there is no way to retrieve test status in MUnit - https://github.com/scalameta/munit/issues/119
    startedContainers.foreach(stopContainers)
  }
}
