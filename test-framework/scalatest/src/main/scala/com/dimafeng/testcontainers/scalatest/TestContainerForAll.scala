package com.dimafeng.testcontainers.scalatest

import com.dimafeng.testcontainers.ContainerDef
import org.scalatest.Suite

/**
  * Starts a single container before all tests and stop it after all tests
  */
trait TestContainerForAll extends TestContainersForAll { self: Suite =>

  val containerDef: ContainerDef

  final override type Containers = containerDef.Container

  override def startContainers(): containerDef.Container = {
    containerDef.start()
  }
}
