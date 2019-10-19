package com.dimafeng.testcontainers.scalatest

import com.dimafeng.testcontainers.ContainerDef
import org.scalatest.Suite

/**
  * Starts a single container before each test and stop it after each test
  */
trait TestContainerForEach extends TestContainersForEach { self: Suite =>

  val containerDef: ContainerDef

  final override type Containers = containerDef.Container

  override def startContainers(): containerDef.Container = {
    containerDef.start()
  }
}
