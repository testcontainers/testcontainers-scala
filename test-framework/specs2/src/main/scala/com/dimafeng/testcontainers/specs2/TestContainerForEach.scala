package com.dimafeng.testcontainers.specs2

import com.dimafeng.testcontainers.ContainerDef
import org.specs2.specification.core.SpecificationStructure

trait TestContainerForEach extends TestContainersForEach { self: SpecificationStructure =>
  val containerDef: ContainerDef
  final override type Containers = containerDef.Container
  override def startContainers(): containerDef.Container = containerDef.start()
}