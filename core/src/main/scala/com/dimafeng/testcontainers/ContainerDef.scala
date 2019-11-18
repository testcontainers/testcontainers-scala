package com.dimafeng.testcontainers

import com.dimafeng.testcontainers.lifecycle.Stoppable
import org.testcontainers.lifecycle.Startable

trait ContainerDef {

  type Container <: Startable with Stoppable

  protected def createContainer(): Container

  def start(): Container = {
    val container = createContainer()
    container.start()
    container
  }
}
