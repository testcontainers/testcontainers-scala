package com.dimafeng.testcontainers.munit

import java.util.Optional

import com.dimafeng.testcontainers.{ContainerDef, SingleContainer}
import com.dimafeng.testcontainers.munit.SampleContainer.SampleJavaContainer
import com.dimafeng.testcontainers.lifecycle.TestLifecycleAware
import org.testcontainers.containers.{GenericContainer => JavaGenericContainer}
import org.testcontainers.lifecycle.{TestDescription, TestLifecycleAware => JavaTestLifecycleAware}

case class SampleContainer(sampleJavaContainer: SampleJavaContainer)
  extends SingleContainer[SampleJavaContainer] with TestLifecycleAware {
  override implicit val container: SampleJavaContainer = sampleJavaContainer

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
    override protected def createContainer(): SampleContainer = {
      SampleContainer(sampleJavaContainer)
    }
  }
}
