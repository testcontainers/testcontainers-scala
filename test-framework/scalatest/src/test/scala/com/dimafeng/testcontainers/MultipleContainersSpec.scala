package com.dimafeng.testcontainers

import java.util.Optional

import com.dimafeng.testcontainers.ContainerSpec.{SampleContainer, SampleOTCContainer}
import com.dimafeng.testcontainers.MultipleContainersSpec.{InitializableContainer, TestSpec}
import org.junit.runner.Description
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.verify
import org.scalatest.{Args, FlatSpec, Reporter}
import org.scalatestplus.mockito.MockitoSugar

class MultipleContainersSpec extends BaseSpec[ForEachTestContainer] {
  it should "call all expected methods of the multiple containers" in {
    val container1 = mock[SampleOTCContainer]
    val container2 = mock[SampleOTCContainer]

    val containers = MultipleContainers(new SampleContainer(container1), new SampleContainer(container2))

    new TestSpec({
      assert(1 == 1)
    }, containers).run(None, Args(mock[Reporter]))

    verify(container1).beforeTest(any())
    verify(container1).start()
    verify(container1).afterTest(any(), ArgumentMatchers.eq(Optional.empty()))
    verify(container1).stop()

    verify(container2).beforeTest(any())
    verify(container2).start()
    verify(container2).afterTest(any(), ArgumentMatchers.eq(Optional.empty()))
    verify(container2).stop()
  }

  /**
    * See #12
    */
  it should "initialize containers lazily in `MultipleContainers` to let second container be depended on start data of the first one" in {
    lazy val container1 = new InitializableContainer("after start value")
    lazy val container2 = new InitializableContainer(container1.value)

    val containers = MultipleContainers(container1, container2)

    new TestSpec({
      assert(1 == 1)
    }, containers).run(None, Args(mock[Reporter]))

    assert(container1.value == "after start value")
    assert(container2.value == "after start value")
  }
}

object MultipleContainersSpec {

  class InitializableContainer(valueToBeSetAfterStart: String) extends SingleContainer[SampleOTCContainer] with MockitoSugar {
    override implicit val container: SampleOTCContainer = mock[SampleOTCContainer]
    var value: String = _

    override def start(): Unit = {
      value = valueToBeSetAfterStart
    }
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
