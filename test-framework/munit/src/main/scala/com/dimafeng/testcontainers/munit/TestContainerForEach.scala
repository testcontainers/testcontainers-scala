package com.dimafeng.testcontainers.munit

import com.dimafeng.testcontainers.ContainerDef
import munit.Suite

/**
 * Starts a single container before each test and stops it after each test
 *
 * Example:
 * {{{
 * class MysqlSpec extends FunSuite with TestContainerForEach {
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
 * - If you override beforeEach() without calling super.beforeEach() your container won't start
 * - If you override afterEach() without calling super.afterEach() your container won't stop
 * }}}
 */
trait TestContainerForEach extends TestContainersForEach { self: Suite =>
  val containerDef: ContainerDef

  final override type Containers = containerDef.Container

  override def startContainers(): containerDef.Container = containerDef.start()
}
