package com.dimafeng.testcontainers.munit

import java.util.Optional

import com.dimafeng.testcontainers.lifecycle.and
import com.dimafeng.testcontainers.munit.SampleContainer.SampleJavaContainer
import com.dimafeng.testcontainers.munit.TestContainersForAllSpec._
import munit.{FunSuite, MUnitRunner, Suite}
import org.junit.runner.notification.RunNotifier
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify}
import org.mockito.{ArgumentMatchers, Mockito}
import org.scalatestplus.mockito.MockitoSugar

class TestContainersForAllSpec extends FunSuite with MockitoSugar {
  test("call all appropriate methods of the containers") {
    val container1 = mock[SampleJavaContainer]
    val container2 = mock[SampleJavaContainer]

    val spec = new TestSpec(assert(cond = true), container1, container2)
    run(spec)

    verify(container1).beforeTest(any())
    verify(container1).start()
    verify(container1).afterTest(any(), ArgumentMatchers.eq(Optional.empty()))
    verify(container1).stop()

    verify(container2).beforeTest(any())
    verify(container2).start()
    verify(container2).afterTest(any(), ArgumentMatchers.eq(Optional.empty()))
    verify(container2).stop()
  }

  test("call all appropriate methods of the containers if assertion fails") {
    val container1 = mock[SampleJavaContainer]
    val container2 = mock[SampleJavaContainer]

    val spec = new TestSpec(assert(cond = false), container1, container2)
    run(spec)

    verify(container1).beforeTest(any())
    verify(container1).start()
    verify(container1).afterTest(any(), ArgumentMatchers.eq(Optional.empty()))
    verify(container1).stop()

    verify(container2).beforeTest(any())
    verify(container2).start()
    verify(container2).afterTest(any(), ArgumentMatchers.eq(Optional.empty()))
    verify(container2).stop()
  }

  test("start and stop containers only once") {
    val container1 = mock[SampleJavaContainer]
    val container2 = mock[SampleJavaContainer]

    val spec = new MultipleTestsSpec(assert(cond = true), container1, container2)
    run(spec)

    verify(container1, times(2)).beforeTest(any())
    verify(container1).start()
    verify(container1, times(2)).afterTest(any(), any())
    verify(container1).stop()

    verify(container2, times(2)).beforeTest(any())
    verify(container2).start()
    verify(container2, times(2)).afterTest(any(), any())
    verify(container2).stop()
  }

  test("call afterContainersStart() and beforeContainersStop()") {
    val container1 = mock[SampleJavaContainer]
    val container2 = mock[SampleJavaContainer]

    // Mockito somehow messed up internal state, so we can't use `spy` here.
    @volatile var afterStartCalled = false
    @volatile var beforeStopCalled = false

    val spec = new MultipleTestsSpec(assert(true), container1, container2) {
      override def afterContainersStart(containers: Containers): Unit = {
        super.afterContainersStart(containers)
        afterStartCalled = true
      }

      override def beforeContainersStop(containers: Containers): Unit = {
        super.beforeContainersStop(containers)
        beforeStopCalled = true
      }
    }

    run(spec)

    assert(afterStartCalled && beforeStopCalled)
  }

  test("call beforeContainersStop() and stop container if error thrown in afterContainersStart()") {
    val container1 = mock[SampleJavaContainer]
    val container2 = mock[SampleJavaContainer]

    @volatile var afterStartCalled = false
    @volatile var beforeStopCalled = false

    val spec: MultipleTestsSpec = new MultipleTestsSpec(assert(cond = true), container1, container2) {
      override def afterContainersStart(containers: Containers): Unit = {
        afterStartCalled = true
        throw new RuntimeException("Test")
      }

      override def beforeContainersStop(containers: Containers): Unit = {
        super.beforeContainersStop(containers)
        beforeStopCalled = true
      }
    }

    run(spec)

    verify(container1, times(0)).beforeTest(any())
    verify(container1).start()
    verify(container1, times(0)).afterTest(any(), any())
    verify(container1).stop()

    verify(container2, times(0)).beforeTest(any())
    verify(container2).start()
    verify(container2, times(0)).afterTest(any(), any())
    verify(container2).stop()

    assert(afterStartCalled && beforeStopCalled)
  }

  test("not start container if all tests are ignored") {
    val container1 = mock[SampleJavaContainer]
    val container2 = mock[SampleJavaContainer]

    @volatile var called = false

    val spec = new TestSpecWithAllIgnored({called = true}, container1, container2)
    run(spec)

    verify(container1, Mockito.never()).start()
    verify(container2, Mockito.never()).start()

    assert(!called)
  }

  test("not start container for empty suite") {
    val container1 = mock[SampleJavaContainer]
    val container2 = mock[SampleJavaContainer]

    val spec = new EmptySpec(container1, container2)
    run(spec)

    verify(container1, Mockito.never()).start()
    verify(container2, Mockito.never()).start()
  }

  test("not start container if beforeAll is overridden without calling super.beforeAll()") {
    val container1 = mock[SampleJavaContainer]
    val container2 = mock[SampleJavaContainer]

    val spec = new TestSpecBeforeOverride(assert(cond = true), container1, container2)
    run(spec)

    verify(container1, Mockito.never()).beforeTest(any())
    verify(container1, Mockito.never()).start()
    verify(container1, Mockito.never()).afterTest(any(), ArgumentMatchers.eq(Optional.empty()))
    verify(container1, Mockito.never()).stop()

    verify(container2, Mockito.never()).beforeTest(any())
    verify(container2, Mockito.never()).start()
    verify(container2, Mockito.never()).afterTest(any(), ArgumentMatchers.eq(Optional.empty()))
    verify(container2, Mockito.never()).stop()
  }

  test("not stop container if afterAll is overridden without calling super.afterAll()") {
    val container1 = mock[SampleJavaContainer]
    val container2 = mock[SampleJavaContainer]

    val spec = new TestSpecAfterOverride(assert(cond = true), container1, container2)
    run(spec)

    verify(container1).beforeTest(any())
    verify(container1).start()
    verify(container1).afterTest(any(), ArgumentMatchers.eq(Optional.empty()))
    verify(container1, Mockito.never()).stop()

    verify(container2).beforeTest(any())
    verify(container2).start()
    verify(container2).afterTest(any(), ArgumentMatchers.eq(Optional.empty()))
    verify(container2, Mockito.never()).stop()
  }
}

object TestContainersForAllSpec {
  protected abstract class ContainersSpec(
    container1: SampleJavaContainer,
    container2: SampleJavaContainer
  ) extends FunSuite with TestContainersForAll {
    override type Containers = SampleContainer and SampleContainer

    override def startContainers(): Containers = {
      val c1 = SampleContainer.Def(container1).start()
      val c2 = SampleContainer.Def(container2).start()
      c1 and c2
    }
  }

  protected class TestSpec(testBody: => Unit, container1: SampleJavaContainer, container2: SampleJavaContainer)
    extends ContainersSpec(container1, container2) {

    test("test") {
      withContainers { case c1 and c2 =>
        assert(
          c1.underlyingUnsafeContainer == container1 &&
            c2.underlyingUnsafeContainer == container2
        )
        testBody
      }
    }
  }

  protected class MultipleTestsSpec(testBody: => Unit, container1: SampleJavaContainer, container2: SampleJavaContainer)
    extends ContainersSpec(container1, container2) {

    test("test1") {
      withContainers { case c1 and c2 =>
        assert(
          c1.underlyingUnsafeContainer == container1 &&
            c2.underlyingUnsafeContainer == container2
        )
        testBody
      }
    }

    test("test2") {
      withContainers { case c1 and c2 =>
        assert(
          c1.underlyingUnsafeContainer == container1 &&
            c2.underlyingUnsafeContainer == container2
        )
        testBody
      }
    }
  }

  protected class TestSpecWithAllIgnored(testBody: => Unit, container1: SampleJavaContainer, container2: SampleJavaContainer)
    extends ContainersSpec(container1, container2) {

    test("test".ignore) { testBody }
  }

  protected class TestSpecBeforeOverride(testBody: => Unit, container1: SampleJavaContainer, container2: SampleJavaContainer)
    extends ContainersSpec(container1, container2) {

    override def beforeAll(): Unit = {}

    test("test") {
      withContainers { _ => testBody }
    }
  }

  protected class TestSpecAfterOverride(testBody: => Unit, container1: SampleJavaContainer, container2: SampleJavaContainer)
    extends ContainersSpec(container1, container2) {

    override def afterAll(): Unit = {}

    test("test") {
      withContainers { _ => testBody }
    }
  }

  protected class EmptySpec(container1: SampleJavaContainer, container2: SampleJavaContainer) extends ContainersSpec(container1, container2)

  private def run(res: Suite): Unit = {
    val notifier = new RunNotifier()
    new MUnitRunner(res.asInstanceOf[Suite].getClass, () => res).run(notifier)
  }
}