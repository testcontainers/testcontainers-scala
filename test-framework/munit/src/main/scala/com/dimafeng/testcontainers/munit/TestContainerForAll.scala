package com.dimafeng.testcontainers.munit

import com.dimafeng.testcontainers.ContainerDef
import munit.Suite

/**
 * Starts a single container before all tests and stops it after all tests
 *
 * Example:
 * {{{
 * class MysqlSpec extends FunSuite with TestContainerForAll {
 *
 *   // You need to override `containerDef` with needed container definition
 *   override val containerDef = MySQLContainer.Def()
 *
 *   // To use containers in tests you need to use `withContainers` function
 *   test("test case name") {
 *     withContainers { mysqlContainer =>
 *       // Inside your test body you can do with your container whatever you want to
 *       assert(mysqlContainer.jdbcUrl.nonEmpty)
 *     }
 *   }
 * }
 *
 * Notes:
 * - If you override beforeAll() without calling super.beforeAll() your container won't start
 * - If you override afterAll() without calling super.afterAll() your container won't stop
 * }}}
 */
trait TestContainerForAll extends TestContainersForAll { self: Suite =>
  val containerDef: ContainerDef

  final override type Containers = containerDef.Container

  override def startContainers(): containerDef.Container = containerDef.start()
}
