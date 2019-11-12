package com.dimafeng.testcontainers.scalatest

import com.dimafeng.testcontainers.ContainerDef
import org.scalatest.Suite

/**
  * Starts a single container before each test and stop it after each test
  *
  * Example:
  * {{{
  * class MysqlSpec extends FlatSpec with TestContainerForEach {
  *
  *   // You need to override `containerDef` with needed container definition
  *   override val containerDef = MySQLContainer.Def()
  *
  *   // To use containers in tests you need to use `withContainers` function
  *   it should "test" in withContainers { mysqlContainer =>
  *     // Inside your test body you can do with your container whatever you want to
  *     assert(mysqlContainer.jdbcUrl.nonEmpty)
  *   }
  * }
  * }}}
  */
trait TestContainerForEach extends TestContainersForEach { self: Suite =>

  val containerDef: ContainerDef

  final override type Containers = containerDef.Container

  override def startContainers(): containerDef.Container = {
    containerDef.start()
  }
}
