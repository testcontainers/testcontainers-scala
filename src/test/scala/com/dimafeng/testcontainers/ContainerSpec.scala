package com.dimafeng.testcontainers

import com.dimafeng.testcontainers.ContainerSpec._
import org.junit.runner.Description
import org.mockito.Matchers.any
import org.mockito.Mockito
import org.mockito.Mockito.{times, verify}
import org.scalatest.{Args, FlatSpec, Reporter}
import org.testcontainers.containers.{GenericContainer => OTCGenericContainer}

class ContainerSpec extends BaseSpec[ForEachTestContainer] {

  behavior of "Single Container inside suite"

  it should "call all appropriate methods of the container" in {
    val container= mock[SampleOTCContainer]

    new TestSpec({
      assert(1 == 1)
    }) {
      new SampleContainer(container)
    }.run(None, Args(mock[Reporter]))

    verify(container).starting(any())
    verify(container, times(0)).failed(any(), any())
    verify(container).finished(any())
    verify(container).succeeded(any())
  }

  it should "call all appropriate methods of the container if assertion fails" in {
    val container = mock[SampleOTCContainer]

    new TestSpec({
      assert(1 == 2)
    }) {
      new SampleContainer(container)
    }.run(None, Args(mock[Reporter]))

    verify(container).starting(any())
    verify(container).failed(any(), any())
    verify(container).finished(any())
    verify(container, times(0)).succeeded(any())
  }

  it should "start and stop container only once" in {
    val container = mock[SampleOTCContainer]

    new MultipleTestsSpec({
      assert(1 == 1)
    }) {
      new SampleContainer(container)
    }.run(None, Args(mock[Reporter]))

    verify(container).starting(any())
    verify(container, times(0)).failed(any(), any())
    verify(container).finished(any())
    verify(container, times(0)).succeeded(any())
  }

  it should "call afterStart() and beforeStop() in ForEachTestContainer" in {
    val container = mock[SampleOTCContainer]

    class TestSpecForMock extends TestSpec({}) {
      new SampleContainer(container)
    }

    val specForEach = Mockito.spy(new TestSpecForMock)
    specForEach.run(None, Args(mock[Reporter]))

    verify(specForEach).afterStart()
    verify(specForEach).beforeStop()
  }

  it should "call afterStart() and beforeStop() in ForAllTestContainer" in {
    val container = mock[SampleOTCContainer]

    class MultipleTestsSpecForMock extends MultipleTestsSpec({}) {
      new SampleContainer(container)
    }

    val specForAll = Mockito.spy(new MultipleTestsSpecForMock)
    specForAll.run(None, Args(mock[Reporter]))

    verify(specForAll).afterStart()
    verify(specForAll).beforeStop()
  }

  it should "call beforeStop() and stop container if error thrown in afterStart() in ForEachTestContainer" in {
    val container = mock[SampleOTCContainer]

    class TestSpecWithFailedAfterStartForMock extends TestSpecWithFailedAfterStart({}) {
      new SampleContainer(container)
    }

    val specForEach = Mockito.spy(new TestSpecWithFailedAfterStartForMock)
    intercept[RuntimeException] {
      specForEach.run(None, Args(mock[Reporter]))
    }
    verify(container).starting(any())
    verify(specForEach).afterStart()
    verify(container).failed(any(), any())
    verify(specForEach).beforeStop()
    verify(container).finished(any())
    verify(container, times(0)).succeeded(any())
  }

  it should "call beforeStop() and stop container if error thrown in afterStart() in ForAllTestContainer" in {
    val container = mock[SampleOTCContainer]

    class MultipleTestsSpecWithFailedAfterStartForMock extends MultipleTestsSpecWithFailedAfterStart({println("TEST!")}) {
      new SampleContainer(container)
    }

    val specForAll = Mockito.spy(new MultipleTestsSpecWithFailedAfterStartForMock)
    intercept[RuntimeException] {
      specForAll.run(None, Args(mock[Reporter]))
    }
    verify(container, times(1)).starting(any())
    verify(specForAll).afterStart()
    verify(specForAll).beforeStop()
    verify(container, times(1)).finished(any())
    verify(container, times(0)).succeeded(any())
  }

  it should "not start container if all tests are ignored" in {
    val container = mock[SampleOTCContainer]

    class TestSpecWithAllIgnoredForMock extends TestSpecWithAllIgnored({}) {
      new SampleContainer(container)
    }

    val specForAll = Mockito.spy(new TestSpecWithAllIgnoredForMock)
    specForAll.run(None, Args(mock[Reporter]))

    verify(container, Mockito.never()).starting(any())
  }
}

object ContainerSpec {

  class TestSpec(testBody: => Unit) extends FlatSpec with ForEachTestContainer {
    it should "test" in {
      testBody
    }
  }

  class TestSpecWithFailedAfterStart(testBody: => Unit) extends FlatSpec with ForEachTestContainer {
    override def afterStart(): Unit = throw new RuntimeException("something wrong in afterStart()")

    it should "test" in {
      testBody
    }
  }

  class MultipleTestsSpec(testBody: => Unit) extends FlatSpec with ForAllTestContainer {

    it should "test1" in {
      testBody
    }

    it should "test2" in {
      testBody
    }
  }

  class MultipleTestsSpecWithFailedAfterStart(testBody: => Unit) extends FlatSpec with ForAllTestContainer {
    override def afterStart(): Unit = throw new RuntimeException("something wrong in afterStart()")

    it should "test1" in {
      testBody
    }

    it should "test2" in {
      testBody
    }
  }

  class TestSpecWithAllIgnored(testBody: => Unit) extends FlatSpec with ForAllTestContainer {
    it should "test" ignore {
      testBody
    }
  }

  class SampleOTCContainer extends OTCGenericContainer {
    override def starting(description: Description): Unit = { println("starting") }

    override def failed(e: Throwable, description: Description): Unit = { println("failed") }

    override def finished(description: Description): Unit = { println("finished") }

    override def succeeded(description: Description): Unit = { println("succeeded") }
  }

  class SampleContainer(sampleOTCContainer: SampleOTCContainer)(implicit testContainersContext: TestContainersContext)
    extends SingleContainer[SampleOTCContainer] {
    override implicit val container: SampleOTCContainer = sampleOTCContainer
  }
}
