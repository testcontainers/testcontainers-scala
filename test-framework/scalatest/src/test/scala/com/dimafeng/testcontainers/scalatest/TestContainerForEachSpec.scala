package com.dimafeng.testcontainers.scalatest

import java.util.Optional

import com.dimafeng.testcontainers.{BaseSpec, ContainerDef, SampleContainer, SampleJavaContainer}
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito.{times, verify}
import org.mockito.{ArgumentCaptor, ArgumentMatchers, Mockito}
import org.scalatest.{Args, FlatSpec, Reporter}

class TestContainerForEachSpec extends BaseSpec[TestContainerForEach] {

  import TestContainerForEachSpec._

  it should "call all appropriate methods of the container" in {
    val container = mock[SampleJavaContainer]

    new TestSpec({
      assert(1 == 1)
    }, SampleContainer.Def(container)).run(None, Args(mock[Reporter]))

    verify(container).beforeTest(any())
    verify(container).start()
    verify(container).afterTest(any(), ArgumentMatchers.eq(Optional.empty()))
    verify(container).stop()
  }

  it should "call all appropriate methods of the container if assertion fails" in {
    val container = mock[SampleJavaContainer]

    new TestSpec({
      assert(1 == 2)
    }, SampleContainer.Def(container)).run(None, Args(mock[Reporter]))

    val captor = ArgumentCaptor.forClass[Optional[Throwable], Optional[Throwable]](classOf[Optional[Throwable]])
    verify(container).beforeTest(any())
    verify(container).start()
    verify(container).afterTest(any(), captor.capture())
    assert(captor.getValue.isPresent)
    verify(container).stop()
  }

  it should "start and stop container before and after each test case" in {
    val container = mock[SampleJavaContainer]

    new MultipleTestsSpec({
      assert(1 == 1)
    }, SampleContainer.Def(container)).run(None, Args(mock[Reporter]))

    verify(container, times(2)).beforeTest(any())
    verify(container, times(2)).start()
    verify(container, times(2)).afterTest(any(), any())
    verify(container, times(2)).stop()
  }

  it should "call afterContainersStart() and beforeContainersStop()" in {
    val container = mock[SampleJavaContainer]

    val spec = Mockito.spy(new MultipleTestsSpec({}, SampleContainer.Def(container)))
    spec.run(None, Args(mock[Reporter]))

    verify(spec, times(2)).afterContainersStart(any())
    verify(spec, times(2)).beforeContainersStop(any())
  }

  it should "call beforeContainersStop() and stop container if error thrown in afterContainersStart()" in {
    val container = mock[SampleJavaContainer]

    @volatile var beforeContainersStopCalled = false

    val spec = new MultipleTestsSpecWithFailedAfterStart({}, SampleContainer.Def(container), () => {
      beforeContainersStopCalled = true
    })
    intercept[RuntimeException] {
      spec.run(None, Args(mock[Reporter]))
    }
    verify(container, times(0)).beforeTest(any())
    verify(container).start()
    verify(container, times(0)).afterTest(any(), any())
    verify(container).stop()
    assert(beforeContainersStopCalled)
  }

  it should "not start container if all tests are ignored" in {
    val container = mock[SampleJavaContainer]
    val spec = Mockito.spy(new TestSpecWithAllIgnored({}, SampleContainer.Def(container)))
    spec.run(None, Args(mock[Reporter]))

    verify(container, Mockito.never()).start()
  }
}

object TestContainerForEachSpec {

  protected class TestSpec(testBody: => Unit, contDef: ContainerDef)
    extends FlatSpec with TestContainerForEach {

    override val containerDef: ContainerDef = contDef

    it should "test" in {
      testBody
    }
  }

  protected class MultipleTestsSpec(testBody: => Unit, contDef: ContainerDef) extends FlatSpec with TestContainerForEach {

    override val containerDef: ContainerDef = contDef

    it should "test1" in {
      testBody
    }

    it should "test2" in {
      testBody
    }
  }

  protected class MultipleTestsSpecWithFailedAfterStart(
    testBody: => Unit,
    contDef: ContainerDef,
    beforeContStop: () => Unit
  ) extends FlatSpec with TestContainerForEach {

    override val containerDef: ContainerDef = contDef

    override def afterContainersStart(containers: Containers): Unit =
      throw new RuntimeException("something wrong in afterContainersStart()")

    override def beforeContainersStop(containers: containerDef.Container): Unit = beforeContStop()

    it should "test1" in {
      testBody
    }

    it should "test2" in {
      testBody
    }
  }

  protected class TestSpecWithAllIgnored(testBody: => Unit, contDef: ContainerDef) extends FlatSpec with TestContainerForEach {

    override val containerDef: ContainerDef = contDef

    it should "test" ignore {
      testBody
    }
  }
}
