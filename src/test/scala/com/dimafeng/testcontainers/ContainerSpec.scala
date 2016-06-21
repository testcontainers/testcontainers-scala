package com.dimafeng.testcontainers

import org.junit.runner.{Description, RunWith}
import org.mockito.Matchers.any
import org.mockito.{Matchers, Mockito}
import org.mockito.Mockito.{times, verify}
import org.scalatest.mock.MockitoSugar._
import org.scalatest.{Reporter, Args, FlatSpec}
import org.scalatest.junit.JUnitRunner
import org.testcontainers.containers.GenericContainer
import com.dimafeng.testcontainers.ContainerSpec._

@RunWith(classOf[JUnitRunner])
class ContainerSpec extends FlatSpec {
  behavior of "ForEachTestContainer"

  it should "call all appropriate methods of the container" in {
    val container = Mockito.mock(classOf[SampleContainer])

    new TestSpec({
      assert(1 == 1)
    }, container).run(None, Args(mock[Reporter]))

    verify(container).starting(any())
    verify(container, times(0)).failed(any(), any())
    verify(container).finished(any())
    verify(container).succeeded(any())
  }

  it should "call all appropriate methods of the container if assertion fails" in {
    val container = Mockito.mock(classOf[SampleContainer])

    new TestSpec({
      assert(1 == 2)
    }, container).run(None, Args(mock[Reporter]))

    verify(container).starting(any())
    verify(container).failed(any(), any())
    verify(container).finished(any())
    verify(container, times(0)).succeeded(any())
  }

  it should "call all appropriate methods of the multiple containers" in {
    val container1 = Mockito.mock(classOf[SampleContainer])
    val container2 = Mockito.mock(classOf[SampleContainer])

    new TestSpec({
      assert(1 == 1)
    }, container1, container2).run(None, Args(mock[Reporter]))

    verify(container1).starting(any())
    verify(container1, times(0)).failed(any(), any())
    verify(container1).finished(any())
    verify(container1).succeeded(any())
    verify(container2).starting(any())
    verify(container2, times(0)).failed(any(), any())
    verify(container2).finished(any())
    verify(container2).succeeded(any())
  }

  it should "start and stop container only once" in {
    val container = Mockito.mock(classOf[SampleContainer])

    new MultipleTestsSpec({
      assert(1 == 1)
    }, container).run(None, Args(mock[Reporter]))

    verify(container).starting(any())
    verify(container, times(0)).failed(any(), any())
    verify(container).finished(any())
    verify(container, times(0)).succeeded(any())
  }
}

object ContainerSpec {

  private class TestSpec(testBody: => Unit, genericContainers: GenericContainer[_]*) extends FlatSpec with ForEachTestContainer {
    override val container = Container(genericContainers: _*)

    it should "test" in {
      testBody
    }
  }

  private class MultipleTestsSpec(testBody: => Unit, genericContainers: GenericContainer[_]*) extends FlatSpec with ForAllTestContainer {
    override val container = Container(genericContainers: _*)

    it should "test1" in {
      testBody
    }

    it should "test2" in {
      testBody
    }
  }

  private class SampleContainer extends GenericContainer {
    override def starting(description: Description): Unit = {}

    override def failed(e: Throwable, description: Description): Unit = {}

    override def finished(description: Description): Unit = {}

    override def succeeded(description: Description): Unit = {}
  }

}