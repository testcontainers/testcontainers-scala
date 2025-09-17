package com.dimafeng.testcontainers.specs2

import java.util.Optional
import com.dimafeng.testcontainers.{ContainerDef, SingleContainer}
import com.dimafeng.testcontainers.lifecycle.TestLifecycleAware
import org.testcontainers.containers.{GenericContainer => JavaGenericContainer}
import org.testcontainers.lifecycle.{TestDescription, TestLifecycleAware => JavaTestLifecycleAware}

case class SampleContainer(sampleJavaContainer: SampleContainer.SampleJavaContainer)
  extends SingleContainer[SampleContainer.SampleJavaContainer] with TestLifecycleAware {
  override implicit val container: SampleContainer.SampleJavaContainer = sampleJavaContainer

  override def beforeTest(description: TestDescription): Unit = {
    container.beforeTest(description)
  }

  override def afterTest(description: TestDescription, throwable: Option[Throwable]): Unit = {
    container.afterTest(description, throwable.fold[Optional[Throwable]](Optional.empty())(Optional.of))
  }
}

object SampleContainer {
  class SampleJavaContainer extends JavaGenericContainer[SampleJavaContainer] with JavaTestLifecycleAware {
    override def beforeTest(description: TestDescription): Unit = {}
    override def afterTest(description: TestDescription, throwable: Optional[Throwable]): Unit = {}
    override def start(): Unit = {}
    override def stop(): Unit = {}
  }

  case class Def(sampleJavaContainer: SampleJavaContainer) extends ContainerDef {
    override type Container = SampleContainer
    override protected def createContainer(): SampleContainer = SampleContainer(sampleJavaContainer)
  }
}