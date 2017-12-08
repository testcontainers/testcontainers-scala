package com.dimafeng.testcontainers

import org.junit.runner.{Description, RunWith}
import org.mockito.Matchers.any
import org.mockito.Mockito
import org.mockito.Mockito.{times, verify}
import org.scalatest.mockito.MockitoSugar._
import org.scalatest.{Args, FlatSpec, Reporter}
import org.scalatest.junit.JUnitRunner
import org.testcontainers.containers.{GenericContainer => OTCGenericContainer}
import com.dimafeng.testcontainers.ContainerSpec._

@RunWith(classOf[JUnitRunner])
class ContainerSpec extends FlatSpec {
  behavior of "ForEachTestContainer"

  it should "call all appropriate methods of the container" in {
    val container = Mockito.mock(classOf[SampleOTCContainer])

    new TestSpec({
      assert(1 == 1)
    }, new SampleContainer(container)).run(None, Args(mock[Reporter]))

    verify(container).starting(any())
    verify(container, times(0)).failed(any(), any())
    verify(container).finished(any())
    verify(container).succeeded(any())
  }

  it should "call all appropriate methods of the container if assertion fails" in {
    val container = Mockito.mock(classOf[SampleOTCContainer])

    new TestSpec({
      assert(1 == 2)
    }, new SampleContainer(container)).run(None, Args(mock[Reporter]))

    verify(container).starting(any())
    verify(container).failed(any(), any())
    verify(container).finished(any())
    verify(container, times(0)).succeeded(any())
  }

  it should "call all appropriate methods of the multiple containers" in {
    val container1 = Mockito.mock(classOf[SampleOTCContainer])
    val container2 = Mockito.mock(classOf[SampleOTCContainer])

    val containers = MultipleContainers(new SampleContainer(container1), new SampleContainer(container2))

    new TestSpec({
      assert(1 == 1)
    }, containers).run(None, Args(mock[Reporter]))

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
    val container = Mockito.mock(classOf[SampleOTCContainer])

    new MultipleTestsSpec({
      assert(1 == 1)
    }, new SampleContainer(container)).run(None, Args(mock[Reporter]))

    verify(container).starting(any())
    verify(container, times(0)).failed(any(), any())
    verify(container).finished(any())
    verify(container, times(0)).succeeded(any())
  }

  it should "call afterStart() and beforeStop()" in {
    val container = Mockito.mock(classOf[SampleOTCContainer])

    // ForEach
    val specForEach = Mockito.spy(new TestSpec({}, new SampleContainer(container)))
    specForEach.run(None, Args(mock[Reporter]))

    verify(specForEach).afterStart()
    verify(specForEach).beforeStop()

    // ForAll

    val specForAll = Mockito.spy(new MultipleTestsSpec({}, new SampleContainer(container)))
    specForAll.run(None, Args(mock[Reporter]))

    verify(specForAll).afterStart()
    verify(specForAll).beforeStop()
  }

  it should "call beforeStop() and stop container if error thrown in afterStart()" in {
    val container = Mockito.mock(classOf[SampleOTCContainer])

    // ForEach
    val specForEach = Mockito.spy(new TestSpecWithFailedAfterStart({}, new SampleContainer(container)))
    intercept[RuntimeException] {
      specForEach.run(None, Args(mock[Reporter]))
    }
    verify(container).starting(any())
    verify(specForEach).afterStart()
    verify(container).failed(any(), any())
    verify(specForEach).beforeStop()
    verify(container).finished(any())
    verify(container, times(0)).succeeded(any())

    // ForAll
    val specForAll = Mockito.spy(new MultipleTestsSpecWithFailedAfterStart({}, new SampleContainer(container)))
    intercept[RuntimeException] {
      specForAll.run(None, Args(mock[Reporter]))
    }
    verify(container, times(2)).starting(any())
    verify(specForAll).afterStart()
    verify(specForAll).beforeStop()
    verify(container, times(2)).finished(any())
    verify(container, times(0)).succeeded(any())
  }

  it should "not start container if all tests are ignored" in {
    val container = Mockito.mock(classOf[SampleOTCContainer])
    val specForAll = Mockito.spy(new TestSpecWithAllIgnored({}, new SampleContainer(container)))
    specForAll.run(None, Args(mock[Reporter]))

    verify(container, Mockito.never()).starting(any())
  }
}

object ContainerSpec {

  private class TestSpec(testBody: => Unit, _container: Container) extends FlatSpec with ForEachTestContainer {
    override val container = _container

    it should "test" in {
      testBody
    }
  }

  private class TestSpecWithFailedAfterStart(testBody: => Unit, _container: Container) extends FlatSpec with ForEachTestContainer {
    override val container = _container
    override def afterStart(): Unit = throw new RuntimeException("something wrong in afterStart()")

    it should "test" in {
      testBody
    }
  }

  private class MultipleTestsSpec(testBody: => Unit, _container: Container) extends FlatSpec with ForAllTestContainer {
    override val container = _container

    it should "test1" in {
      testBody
    }

    it should "test2" in {
      testBody
    }
  }

  private class MultipleTestsSpecWithFailedAfterStart(testBody: => Unit, _container: Container) extends FlatSpec with ForAllTestContainer {
    override val container = _container
    override def afterStart(): Unit = throw new RuntimeException("something wrong in afterStart()")

    it should "test1" in {
      testBody
    }

    it should "test2" in {
      testBody
    }
  }

  private class TestSpecWithAllIgnored(testBody: => Unit, _container: Container) extends FlatSpec with ForAllTestContainer {
    override val container = _container

    it should "test" ignore {
      testBody
    }
  }

  private class SampleOTCContainer extends OTCGenericContainer {
    override def starting(description: Description): Unit = { println("starting") }

    override def failed(e: Throwable, description: Description): Unit = { println("failed") }

    override def finished(description: Description): Unit = { println("finished") }

    override def succeeded(description: Description): Unit = { println("succeeded") }
  }

  private class SampleContainer(sampleOTCContainer: SampleOTCContainer) extends SingleContainer[SampleOTCContainer] {
    override implicit val container: SampleOTCContainer = sampleOTCContainer
  }
}