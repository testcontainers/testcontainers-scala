package com.dimafeng.testcontainers

import org.testcontainers.containers.{GenericContainer => JavaGenericContainer}

trait ContainerDef {

  type Container <: com.dimafeng.testcontainers.Container

  protected def createContainer(): Container

  def start(): Container = {
    val container = createContainer()
    container.underlyingUnsafeContainer.start()
    container
  }
}
