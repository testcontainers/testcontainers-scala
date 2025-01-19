package com.dimafeng.testcontainers.munit.fixtures

import java.util.Optional

import com.dimafeng.testcontainers.ContainerDef
import com.dimafeng.testcontainers.munit.SampleContainer.SampleJavaContainer
import com.dimafeng.testcontainers.munit.fixtures.ForAllTestContainersFixtureSpec._
import com.dimafeng.testcontainers.munit.SampleContainer
import munit.{FunSuite, MUnitRunner, Suite}
import org.junit.runner.notification.RunNotifier
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify}
import org.mockito.{ArgumentMatchers, Mockito}
import org.scalatestplus.mockito.MockitoSugar
import com.dimafeng.testcontainers.SingleContainer
import com.dimafeng.testcontainers.Container

class ForAllTestContainersFixtureSpec extends FunSuite with MockitoSugar {
  test("call all appropriate methods of the container") {
    val container = mock[SampleJavaContainer]

    val spec = new TestSpec(assert(cond = true), SampleContainer(container))
    run(spec)

    verify(container).beforeTest(any())
    verify(container).start()
    verify(container).afterTest(any(), ArgumentMatchers.eq(Optional.empty()))
    verify(container).stop()
  }

  test("call all appropriate methods of the container if assertion fails") {
    val container = mock[SampleJavaContainer]

    val spec = new TestSpec(assert(cond = false), SampleContainer(container))
    run(spec)

    verify(container).beforeTest(any())
    verify(container).start()
    verify(container).afterTest(any(), ArgumentMatchers.eq(Optional.empty()))
    verify(container).stop()
  }

  test("start and stop container only once") {
    val container = mock[SampleJavaContainer]

    val spec = new MultipleTestsSpec(assert(cond = true), SampleContainer(container))
    run(spec)

    verify(container, times(2)).beforeTest(any())
    verify(container).start()
    verify(container, times(2)).afterTest(any(), any())
    verify(container).stop()
  }

}

object ForAllTestContainersFixtureSpec {
  trait ContainerSpec extends FunSuite with TestContainersFixtures

  protected class TestSpec(testBody: => Unit, container: Container) extends ContainerSpec {
    val containerFixture = ForAllContainerFixture(container)
    override def munitFixtures: Seq[Fixture[?]] = List(containerFixture)

    test("test") {testBody}
  }

  protected class MultipleTestsSpec(testBody: => Unit, container: Container) extends ContainerSpec {
    val containerFixture = ForAllContainerFixture(container)
    override def munitFixtures: Seq[Fixture[?]] = List(containerFixture)

    test("test1") {testBody}
    test("test2") {testBody}
  }

  private def run(res: Suite): Unit = {
    val notifier = new RunNotifier()
    new MUnitRunner(res.asInstanceOf[Suite].getClass, () => res).run(notifier)
  }
}
