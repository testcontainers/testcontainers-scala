package com.dimafeng.testcontainers.munit

import java.util.Optional

import com.dimafeng.testcontainers.ContainerDef
import com.dimafeng.testcontainers.munit.SampleContainer.SampleJavaContainer
import com.dimafeng.testcontainers.munit.TestContainerForAllSpec._
import munit.{FunSuite, MUnitRunner, Suite}
import org.junit.runner.notification.RunNotifier
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify, when}
import org.mockito.{ArgumentMatchers, Mockito}
import org.scalatestplus.mockito.MockitoSugar

class TestContainerForAllSpec extends FunSuite with MockitoSugar {
  test("call all appropriate methods of the container") {
    val container = mock[SampleJavaContainer]

    val spec = new TestSpec(assert(cond = true), SampleContainer.Def(container))
    run(spec)

    verify(container).beforeTest(any())
    verify(container).start()
    verify(container).afterTest(any(), ArgumentMatchers.eq(Optional.empty()))
    verify(container).stop()
  }

  test("call all appropriate methods of the container if assertion fails") {
    val container = mock[SampleJavaContainer]

    val spec = new TestSpec(assert(cond = false), SampleContainer.Def(container))
    run(spec)

    verify(container).beforeTest(any())
    verify(container).start()
    verify(container).afterTest(any(), ArgumentMatchers.eq(Optional.empty()))
    verify(container).stop()
  }

  test("start and stop container only once") {
    val container = mock[SampleJavaContainer]

    val spec = new MultipleTestsSpec(assert(cond = true), SampleContainer.Def(container))
    run(spec)

    verify(container, times(2)).beforeTest(any())
    verify(container).start()
    verify(container, times(2)).afterTest(any(), any())
    verify(container).stop()
  }

  test("call afterContainersStart() and beforeContainersStop()") {
    val container = mock[SampleJavaContainer]

    val spec0 = new MultipleTestsSpec({}, SampleContainer.Def(container))
    val spec = Mockito.spy(spec0)
    // bug in mockito ? - spec.munitTests() returns null.
    when(spec.munitTests()).thenReturn(spec0.munitTests());
    run(spec)

    verify(spec).afterContainersStart(any())
    verify(spec).beforeContainersStop(any())
  }

  test("call beforeContainersStop() and stop container if error thrown in afterContainersStart()") {
    val container = mock[SampleJavaContainer]

    @volatile var beforeContainersStopCalled = false

    val spec = new MultipleTestsSpec(assert(cond = true), SampleContainer.Def(container)) {
      override def afterContainersStart(containers: Containers): Unit =
        throw new RuntimeException("something wrong in afterContainersStart()")

      override def beforeContainersStop(containers: containerDef.Container): Unit =
        beforeContainersStopCalled = true
    }

    run(spec)

    verify(container, times(0)).beforeTest(any())
    verify(container).start()
    verify(container, times(0)).afterTest(any(), any())
    verify(container).stop()

    assert(beforeContainersStopCalled)
  }

  test("not start container if all tests are ignored") {
    val container = mock[SampleJavaContainer]
    @volatile var called = false

    val spec = new TestSpecWithAllIgnored({called = true}, SampleContainer.Def(container))
    run(spec)

    verify(container, Mockito.never()).start()

    assert(!called)
  }

  test("not start container if beforeAll is overridden without calling super.beforeAll()") {
    val container = mock[SampleJavaContainer]

    val spec = new TestSpecBeforeOverride(assert(cond = true), SampleContainer.Def(container))
    run(spec)

    verify(container, Mockito.never()).beforeTest(any())
    verify(container, Mockito.never()).start()
    verify(container, Mockito.never()).afterTest(any(), ArgumentMatchers.eq(Optional.empty()))
    verify(container, Mockito.never()).stop()
  }

  test("not stop container if afterAll is overridden without calling super.afterAll()") {
    val container = mock[SampleJavaContainer]

    val spec = new TestSpecAfterOverride(assert(cond = true), SampleContainer.Def(container))
    run(spec)

    verify(container).beforeTest(any())
    verify(container).start()
    verify(container).afterTest(any(), ArgumentMatchers.eq(Optional.empty()))
    verify(container, Mockito.never()).stop()
  }

  test("not start container for empty suite") {
    val container = mock[SampleJavaContainer]

    val spec = new EmptySpec(SampleContainer.Def(container))
    run(spec)

    verify(container, Mockito.never()).start()
  }
}

object TestContainerForAllSpec {
  trait ContainerSpec extends FunSuite with TestContainerForAll

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

    override def beforeAll(): Unit = {}

    test("test") {testBody }
  }

  protected class TestSpecAfterOverride(testBody: => Unit, contDef: ContainerDef) extends ContainerSpec {
    override val containerDef: ContainerDef = contDef

    override def afterAll(): Unit = {}

    test("test") {
      withContainers { _ => testBody }
    }
  }

  protected class EmptySpec(contDef: ContainerDef) extends ContainerSpec {
    override val containerDef: ContainerDef = contDef
  }

  private def run(res: Suite): Unit = {
    val notifier = new RunNotifier()
    new MUnitRunner(res.asInstanceOf[Suite].getClass, () => res).run(notifier)
  }
}
