package com.dimafeng.testcontainers

import java.util.concurrent.CopyOnWriteArrayList

import org.junit.runner.Description

trait TestContainersContext {
  def add(container: Container): Unit
  def startAll()(implicit description: Description): Unit
  def finishAll()(implicit description: Description): Unit
  def failAll(e: Throwable)(implicit description: Description): Unit
  def succeedAll()(implicit description: Description): Unit
}

object TestContainersContext {

  class Empty extends TestContainersContext {
    def add(container: Container): Unit = {}
    def startAll()(implicit description: Description): Unit = {}
    def finishAll()(implicit description: Description): Unit = {}
    def failAll(e: Throwable)(implicit description: Description): Unit = {}
    def succeedAll()(implicit description: Description): Unit = {}
  }

  class Default extends TestContainersContext {

    import scala.collection.JavaConverters._

    private val containers = new CopyOnWriteArrayList[Container]()

    def add(container: Container): Unit = {
      containers.add(container)
    }

    def startAll()(implicit description: Description): Unit = {
      containers.asScala.foreach(_.starting())
    }

    def finishAll()(implicit description: Description): Unit = {
      containers.asScala.reverse.foreach(_.finished())
    }

    def failAll(e: Throwable)(implicit description: Description): Unit = {
      containers.asScala.reverse.foreach(_.failed(e))
    }

    def succeedAll()(implicit description: Description): Unit = {
      containers.asScala.reverse.foreach(_.succeeded())
    }
  }
}
