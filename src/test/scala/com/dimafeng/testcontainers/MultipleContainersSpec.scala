package com.dimafeng.testcontainers

import com.dimafeng.testcontainers.ContainerSpec.{SampleContainer, SampleOTCContainer}
import com.dimafeng.testcontainers.MultipleContainersSpec.{ExampleContainerWithVariable, InitializableContainer, TestSpec}
import org.junit.runner.Description
import org.mockito.Matchers.any
import org.mockito.Mockito.{times, verify}
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{Args, FlatSpec, Reporter}

class MultipleContainersSpec extends BaseSpec[ForEachTestContainer] {
  it should "call all expected methods of the multiple containers" in {
    val container1 = mock[SampleOTCContainer]
    val container2 = mock[SampleOTCContainer]

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

  /**
    * See #12
    */
  it should "initialize containers lazily in `MultipleContainers` to let second container be depended on start data of the first one" in {
    lazy val container1 = new InitializableContainer("after start value")
    lazy val container2 = new InitializableContainer(container1.value)

    val containers = MultipleContainers(LazyContainer(container1), LazyContainer(container2))

    new TestSpec({
      assert(1 == 1)
    }, containers).run(None, Args(mock[Reporter]))

    assert(container1.value == "after start value")
    assert(container2.value == "after start value")
  }

  it should "not initialize containers lazily if they are not defined as lazy" in {
    lazy val container1 = new InitializableContainer("after start value")
    lazy val container2 = new InitializableContainer(container1.value)

    val containers = MultipleContainers(container1, container2)

    new TestSpec({
      assert(1 == 1)
    }, containers).run(None, Args(mock[Reporter]))

    assert(container1.value == "after start value")
    assert(container2.value == null)
  }

  it should "compile HList type inference" in {
    import shapeless._

    val container1 = new InitializableContainer("after start value")
    val container2 = new ExampleContainerWithVariable("test")

    val containers = MultipleContainers(container1, container2)

    val c1 :: c2 :: HNil = containers.containers

    assert(c1.value == null)
    assert(c2.variable == "test")
  }
}

object MultipleContainersSpec {

  class InitializableContainer(valueToBeSetAfterStart: String) extends SingleContainer[SampleOTCContainer] with MockitoSugar {
    override implicit val container: SampleOTCContainer = mock[SampleOTCContainer]
    var value: String = _

    override def finished()(implicit description: Description): Unit = ()

    override def succeeded()(implicit description: Description): Unit = ()

    override def starting()(implicit description: Description): Unit = {
      value = valueToBeSetAfterStart
    }

    override def failed(e: Throwable)(implicit description: Description): Unit = ()
  }

  class ExampleContainerWithVariable(val variable: String) extends SingleContainer[SampleOTCContainer] with MockitoSugar {
    override implicit val container: SampleOTCContainer = mock[SampleOTCContainer]
  }

  protected class TestSpec(testBody: => Unit, _container: Container) extends FlatSpec with ForEachTestContainer {
    override val container = _container

    it should "test" in {
      testBody
    }
  }

}