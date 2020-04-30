package com.dimafeng.testcontainers

import java.util.Optional

import munit.{FunSuite, MUnitRunner, Suite}
import org.junit.runner.notification.RunNotifier
import org.mockito.ArgumentMatchers.any
import org.mockito.{ArgumentMatchers, Mockito}
import org.mockito.Mockito.{times, verify}
import org.scalatestplus.mockito.MockitoSugar
import TestContainerForEachSpec._
import com.dimafeng.testcontainers.SampleContainer.SampleJavaContainer

class TestContainerForEachSpec extends FunSuite with MockitoSugar {
  test("call all appropriate methods of the containers") {
    val container = mock[SampleJavaContainer]

    val spec = new TestSpec(assert(cond = true), SampleContainer.Def(container))
    run(spec)

    verify(container).beforeTest(any())
    verify(container).start()
    verify(container).afterTest(any(), ArgumentMatchers.eq(Optional.empty()))
    verify(container).stop()
  }

  test("call all appropriate methods of the containers if assertion fails") {
    val container = mock[SampleJavaContainer]

    val spec = new TestSpec(assert(cond = false), SampleContainer.Def(container))

    run(spec)

    verify(container).beforeTest(any())
    verify(container).start()
    verify(container).afterTest(any(), ArgumentMatchers.eq(Optional.empty()))
    verify(container).stop()
  }

  test("start and stop containers before and after each test case") {
    val container = mock[SampleJavaContainer]

    val spec = new MultipleTestsSpec(assert(cond = true), SampleContainer.Def(container))
    run(spec)

    verify(container, times(2)).beforeTest(any())
    verify(container, times(2)).start()
    verify(container, times(2)).afterTest(any(), any())
    verify(container, times(2)).stop()
  }

  test("call afterContainersStart() and beforeContainersStop()") {
    val container = mock[SampleJavaContainer]

    // Mockito somehow messed up internal state, so we can't use `spy` here.
    @volatile var afterStartCalled = false
    @volatile var beforeStopCalled = false

    val spec = new MultipleTestsSpec(assert(cond = true), SampleContainer.Def(container)) {
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

  test("call beforeContainersStop() and stop container if error is thrown in afterContainersStart()") {
    val container = mock[SampleJavaContainer]

    @volatile var afterStartCalled = false
    @volatile var beforeStopCalled = false

    val spec = new MultipleTestsSpec(assert(cond = true), SampleContainer.Def(container)) {
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

    verify(container, times(0)).beforeTest(any())
    verify(container, times(2)).start()
    verify(container, times(0)).afterTest(any(), any())
    verify(container, times(2)).stop()

    assert(afterStartCalled && beforeStopCalled)
  }

  test("not start container if all tests are ignored") {
    val container = mock[SampleJavaContainer]

    @volatile var called = false

    val spec = new TestSpecWithAllIgnored({called = true}, SampleContainer.Def(container))
    run(spec)

    verify(container, Mockito.never()).start()
    assert(!called)
  }

  test("not start container if beforeEach is overridden without calling super.beforeEach()") {
    val container = mock[SampleJavaContainer]

    val spec = new TestSpecBeforeOverride(assert(cond = true), SampleContainer.Def(container))
    run(spec)

    verify(container, Mockito.never()).beforeTest(any())
    verify(container, Mockito.never()).start()
    verify(container, Mockito.never()).afterTest(any(), ArgumentMatchers.eq(Optional.empty()))
    verify(container, Mockito.never()).stop()
  }

  test("not stop container if afterAll is overridden without calling super.beforeEach()") {
    val container = mock[SampleJavaContainer]

    val spec = new TestSpecAfterOverride(assert(cond = true), SampleContainer.Def(container))
    run(spec)

    verify(container).beforeTest(any())
    verify(container).start()
    verify(container, Mockito.never()).afterTest(any(), ArgumentMatchers.eq(Optional.empty()))
    verify(container, Mockito.never()).stop()
  }

  test("not start container for empty suite") {
    val container = mock[SampleJavaContainer]

    val spec = new EmptySpec(SampleContainer.Def(container))
    run(spec)

    verify(container, Mockito.never()).start()
  }
}

object TestContainerForEachSpec {
  protected abstract class ContainerSpec extends FunSuite with TestContainerForEach

  protected class TestSpec(testBody: => Unit, contDef: ContainerDef) extends ContainerSpec {
    override val containerDef: ContainerDef = contDef

    test("test") {testBody}
  }

  protected class MultipleTestsSpec(testBody: => Unit, contDef: ContainerDef) extends ContainerSpec {
    override val containerDef: ContainerDef = contDef

    test("test1") {testBody}
    test("test2") {testBody}
  }

  protected class TestSpecWithAllIgnored(testBody: => Unit, contDef: ContainerDef) extends ContainerSpec {
    override val containerDef: ContainerDef = contDef

    test("test".ignore) {testBody}
  }

  protected class TestSpecBeforeOverride(testBody: => Unit, contDef: ContainerDef) extends ContainerSpec {
    override val containerDef: ContainerDef = contDef

    override def beforeEach(context: BeforeEach): Unit = {}

    test("test") {testBody}
  }

  protected class TestSpecAfterOverride(testBody: => Unit, contDef: ContainerDef) extends ContainerSpec {
    override val containerDef: ContainerDef = contDef

    override def afterEach(context: AfterEach): Unit = {}

    test("test") {testBody}
  }

  protected class EmptySpec(contDef: ContainerDef) extends ContainerSpec {
    override val containerDef: ContainerDef = contDef
  }

  private def run(res: Suite): Unit = {
    val notifier = new RunNotifier()
    new MUnitRunner(res.asInstanceOf[Suite].getClass, () => res).run(notifier)
  }
}