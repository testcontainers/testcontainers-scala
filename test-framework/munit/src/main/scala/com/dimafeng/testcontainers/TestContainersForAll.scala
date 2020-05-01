package com.dimafeng.testcontainers

import com.dimafeng.testcontainers.lifecycle.Andable
import munit.Suite

/**
 * Starts containers before all tests and stop then after all tests
 *
 * Example:
 * {{{
 * class ExampleSpec extends FunSuite with TestContainersForAll {
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
 * - If you override beforeAll() without calling super.beforeAll() your containers won't start
 * - If you override afterAll() without calling super.afterAll() your containers won't stop
 * }}}
 */
trait TestContainersForAll extends TestContainersSuite { self: Suite =>
  type Containers <: Andable

  override def beforeAll(): Unit = {
    val tests = self.munitTests()
    val allTestsIgnored = tests.forall(_.tags.contains(munit.Ignore))

    if (tests.nonEmpty && !allTestsIgnored) {
      val containers = startContainers()
      startedContainers = Some(containers)
      try {
        afterContainersStart(containers)
      } catch {
        case e: Throwable =>
          stopContainers(containers)
          throw e
      }
    }
  }

  override def beforeEach(context: BeforeEach): Unit = {
    startedContainers.foreach(beforeTest)
  }

  override def afterEach(context: AfterEach): Unit = {
    startedContainers.foreach(afterTest(_, None)) // TODO is there a way to understand if suite has failed in munit?
  }

  override def afterAll(): Unit = {
    startedContainers.foreach(stopContainers)
  }
}
