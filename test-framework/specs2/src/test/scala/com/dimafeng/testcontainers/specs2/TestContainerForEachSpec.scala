package com.dimafeng.testcontainers.specs2

import com.dimafeng.testcontainers.ContainerDef
import org.specs2.mutable.Specification
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.{mock, verify}
import org.mockito.ArgumentCaptor
import java.util.Optional
import org.specs2.specification.core.SpecificationStructure

class TestContainerForEachSpec extends Specification {
  sequential

  "TestContainerForEach" should {
    "start and stop container for each test" in {
      val container = mock(classOf[SampleContainer.SampleJavaContainer])

      val spec = new MultipleTestsSpec(SampleContainer.Def(container))

      runSpec(spec)

      verify(container, Mockito.times(2)).start()
      verify(container, Mockito.times(2)).beforeTest(any())
      verify(container, Mockito.times(2)).afterTest(any(), any())
      verify(container, Mockito.times(2)).stop()
      ok
    }

    "call afterContainersStart and beforeContainersStop for each test" in {
      val container = mock(classOf[SampleContainer.SampleJavaContainer])
      
      val spec = new LifecycleSpec(SampleContainer.Def(container))
      
      runSpec(spec)
      
      spec.afterStartCount must equalTo(2)
      spec.beforeStopCount must equalTo(2)
    }

    "stop container when afterContainersStart fails" in {
      val container = mock(classOf[SampleContainer.SampleJavaContainer])
      
      val spec = new FailingAfterStartSpec(SampleContainer.Def(container))
      
      // Run spec but expect exception
      try {
        runSpec(spec)
      } catch {
        case _: RuntimeException => // Expected
      }
      
      verify(container, Mockito.times(1)).start()
      verify(container, Mockito.times(1)).stop()
      ok
    }

    "stop container even when test fails" in {
      val container = mock(classOf[SampleContainer.SampleJavaContainer])

      val spec = new FailingTestSpec(SampleContainer.Def(container))

      runSpec(spec)

      verify(container, Mockito.times(1)).start()
      verify(container, Mockito.times(1)).stop()
      
      val optionalCaptor = ArgumentCaptor.forClass(classOf[Optional[Throwable]])
      verify(container, Mockito.times(1)).afterTest(any(), optionalCaptor.capture())
      optionalCaptor.getValue.isPresent must beTrue
    }

    "withContainers should fail before containers are started" in {
      val container = mock(classOf[SampleContainer.SampleJavaContainer])
      val spec = new WithContainersBeforeStartSpec(SampleContainer.Def(container))
      
      spec.withContainers(_ => ()) must throwAn[IllegalStateException](
        message = "'withContainers' method can't be used before all containers are started"
      )
    }
  }

  class MultipleTestsSpec(override val containerDef: ContainerDef) extends Specification with TestContainerForEach {
    "test1" in { withContainers { _ => ok } }
    "test2" in { withContainers { _ => ok } }
  }

  class LifecycleSpec(override val containerDef: ContainerDef) extends Specification with TestContainerForEach {
    var afterStartCount = 0
    var beforeStopCount = 0
    
    override def afterContainersStart(containers: containerDef.Container): Unit = {
      afterStartCount += 1
    }
    
    override def beforeContainersStop(containers: containerDef.Container): Unit = {
      beforeStopCount += 1
    }
    
    "test1" in { withContainers { _ => ok } }
    "test2" in { withContainers { _ => ok } }
  }

  class FailingAfterStartSpec(override val containerDef: ContainerDef) extends Specification with TestContainerForEach {
    override def afterContainersStart(containers: containerDef.Container): Unit = {
      throw new RuntimeException("afterContainersStart failed")
    }
    
    "test" in { withContainers { _ => ok } }
  }

  class FailingTestSpec(override val containerDef: ContainerDef) extends Specification with TestContainerForEach {
    "failing test" in { 
      withContainers { _ => 
        failure("Test failed")
      } 
    }
  }

  class WithContainersBeforeStartSpec(override val containerDef: ContainerDef) extends Specification with TestContainerForEach {
    // Empty spec to test withContainers before start
  }

  private def runSpec(spec: TestContainerForEach): Unit = {
    // Simulate running test cases
    spec match {
      case s: MultipleTestsSpec =>
        // Simulate running 2 tests
        runSingleTest(s)
        runSingleTest(s)
      case s: LifecycleSpec =>
        // Simulate running 2 tests
        runSingleTest(s)
        runSingleTest(s)
      case s: FailingAfterStartSpec =>
        // Simulate running 1 test that fails during setup
        runSingleTest(s)
      case s: FailingTestSpec =>
        // Simulate running 1 failing test
        runSingleTest(s)
    }
  }
  
  private def runSingleTest(spec: TestContainerForEach): Unit = {
    // Note: beforeTest and afterTest are called within the around method implementation
    spec.around {
      spec match {
        case _: FailingTestSpec => failure("Test failed")
        case _ => org.specs2.execute.Success()
      }
    }
  }
}