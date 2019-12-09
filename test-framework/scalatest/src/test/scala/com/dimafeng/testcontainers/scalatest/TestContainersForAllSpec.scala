package com.dimafeng.testcontainers.scalatest

import java.util.Optional

import com.dimafeng.testcontainers.lifecycle.and
import com.dimafeng.testcontainers.{BaseSpec, SampleContainer, SampleJavaContainer}
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito.{times, verify}
import org.mockito.{ArgumentCaptor, ArgumentMatchers, Mockito}
import org.scalatest.{Args, FlatSpec, Reporter}

class TestContainersForAllSpec extends BaseSpec[TestContainersForAll] {

  import TestContainersForAllSpec._

  it should "call all appropriate methods of the containers" in {
    val container1 = mock[SampleJavaContainer]
    val container2 = mock[SampleJavaContainer]

    val res = new TestSpec({
      assert(1 == 1)
    }, container1, container2).run(None, Args(mock[Reporter]))

    assert(res.succeeds())

    verify(container1).beforeTest(any())
    verify(container1).start()
    verify(container1).afterTest(any(), ArgumentMatchers.eq(Optional.empty()))
    verify(container1).stop()

    verify(container2).beforeTest(any())
    verify(container2).start()
    verify(container2).afterTest(any(), ArgumentMatchers.eq(Optional.empty()))
    verify(container2).stop()
  }

  it should "call all appropriate methods of the containers if assertion fails" in {
    val container1 = mock[SampleJavaContainer]
    val container2 = mock[SampleJavaContainer]

    val res = new TestSpec({
      assert(1 == 2)
    }, container1, container2).run(None, Args(mock[Reporter]))

    assert(!res.succeeds())

    val captor1 = ArgumentCaptor.forClass[Optional[Throwable], Optional[Throwable]](classOf[Optional[Throwable]])
    verify(container1).beforeTest(any())
    verify(container1).start()
    verify(container1).afterTest(any(), captor1.capture())
    assert(captor1.getValue.isPresent)
    verify(container1).stop()

    val captor2 = ArgumentCaptor.forClass[Optional[Throwable], Optional[Throwable]](classOf[Optional[Throwable]])
    verify(container2).beforeTest(any())
    verify(container2).start()
    verify(container2).afterTest(any(), captor2.capture())
    assert(captor2.getValue.isPresent)
    verify(container2).stop()
  }

  it should "start and stop containers only once" in {
    val container1 = mock[SampleJavaContainer]
    val container2 = mock[SampleJavaContainer]

    val res = new MultipleTestsSpec({
      assert(1 == 1)
    }, container1, container2).run(None, Args(mock[Reporter]))

    assert(res.succeeds())

    verify(container1, times(2)).beforeTest(any())
    verify(container1).start()
    verify(container1, times(2)).afterTest(any(), any())
    verify(container1).stop()

    verify(container2, times(2)).beforeTest(any())
    verify(container2).start()
    verify(container2, times(2)).afterTest(any(), any())
    verify(container2).stop()
  }

  it should "call afterContainersStart() and beforeContainersStop()" in {
    val container1 = mock[SampleJavaContainer]
    val container2 = mock[SampleJavaContainer]

    // Mockito somehow messed up internal state, so we can't use `spy` here.
    @volatile var afterStartCalled = false
    @volatile var beforeStopCalled = false

    val spec = new MultipleTestsSpec({
      assert(1 == 1)
    }, container1, container2) {
      override def afterContainersStart(containers: Containers): Unit = {
        super.afterContainersStart(containers)
        afterStartCalled = true
      }

      override def beforeContainersStop(containers: Containers): Unit = {
        super.beforeContainersStop(containers)
        beforeStopCalled = true
      }
    }

    val res = spec.run(None, Args(mock[Reporter]))

    assert(res.succeeds() && afterStartCalled && beforeStopCalled)
  }

  it should "call beforeContainersStop() and stop container if error thrown in afterContainersStart()" in {
    val container1 = mock[SampleJavaContainer]
    val container2 = mock[SampleJavaContainer]

    @volatile var afterStartCalled = false
    @volatile var beforeStopCalled = false

    val spec = new MultipleTestsSpec({
      assert(1 == 1)
    }, container1, container2) {
      override def afterContainersStart(containers: Containers): Unit = {
        afterStartCalled = true
        throw new RuntimeException("Test")
      }

      override def beforeContainersStop(containers: Containers): Unit = {
        super.beforeContainersStop(containers)
        beforeStopCalled = true
      }
    }

    val res = intercept[RuntimeException] {
      spec.run(None, Args(mock[Reporter]))
    }

    verify(container1, times(0)).beforeTest(any())
    verify(container1).start()
    verify(container1, times(0)).afterTest(any(), any())
    verify(container1).stop()

    verify(container2, times(0)).beforeTest(any())
    verify(container2).start()
    verify(container2, times(0)).afterTest(any(), any())
    verify(container2).stop()

    assert(res.getMessage === "Test" && afterStartCalled && beforeStopCalled)
  }

  it should "not start container if all tests are ignored" in {
    val container1 = mock[SampleJavaContainer]
    val container2 = mock[SampleJavaContainer]

    @volatile var called = false

    new TestSpecWithAllIgnored({
      called = true
    }, container1, container2) {}.run(None, Args(mock[Reporter]))

    verify(container1, Mockito.never()).start()
    verify(container2, Mockito.never()).start()
    assert(called === false)
  }
}
object TestContainersForAllSpec {

  protected abstract class AbstractTestSpec(
    testBody: => Unit,
    container1: SampleJavaContainer,
    container2: SampleJavaContainer
  ) extends FlatSpec with TestContainersForAll {
    override type Containers = SampleContainer and SampleContainer

    override def startContainers(): Containers = {
      val c1 = SampleContainer.Def(container1).start()
      val c2 = SampleContainer.Def(container2).start()
      c1 and c2
    }
  }

  protected class TestSpec(testBody: => Unit, container1: SampleJavaContainer, container2: SampleJavaContainer)
    extends AbstractTestSpec(testBody, container1, container2) {

    it should "test" in withContainers { case c1 and c2 =>
      assert(
        c1.underlyingUnsafeContainer === container1 &&
        c2.underlyingUnsafeContainer === container2
      )
      testBody
    }
  }

  protected class MultipleTestsSpec(testBody: => Unit, container1: SampleJavaContainer, container2: SampleJavaContainer)
    extends AbstractTestSpec(testBody, container1, container2) {

    it should "test1" in withContainers { case c1 and c2 =>
      assert(
        c1.underlyingUnsafeContainer === container1 &&
        c2.underlyingUnsafeContainer === container2
      )
      testBody
    }

    it should "test2" in withContainers { case c1 and c2 =>
      assert(
        c1.underlyingUnsafeContainer === container1 &&
        c2.underlyingUnsafeContainer === container2
      )
      testBody
    }
  }

  protected class TestSpecWithAllIgnored(testBody: => Unit, container1: SampleJavaContainer, container2: SampleJavaContainer)
    extends AbstractTestSpec(testBody, container1, container2) {

    it should "test" ignore {
      testBody
    }
  }
}
