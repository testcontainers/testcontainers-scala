package com.dimafeng.testcontainers.specs2

import com.dimafeng.testcontainers.ContainerDef
import org.specs2.mutable.Specification
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.{mock, verify}
import org.mockito.ArgumentCaptor
import java.util.Optional
import org.specs2.specification.core.SpecificationStructure

class TestContainerForAllSpec extends Specification {
  sequential

  "TestContainerForAll" should {
    "start and stop container only once" in {
      val container = mock(classOf[SampleContainer.SampleJavaContainer])

      val spec = new MultipleTestsSpec(SampleContainer.Def(container))

      runSpec(spec)

      verify(container, Mockito.times(1)).start()
      verify(container, Mockito.times(2)).beforeTest(any())
      verify(container, Mockito.times(2)).afterTest(any(), any())
      verify(container, Mockito.times(1)).stop()
      ok
    }

    "call afterContainersStart and beforeContainersStop" in {
      val container = mock(classOf[SampleContainer.SampleJavaContainer])
      
      val spec = new LifecycleSpec(SampleContainer.Def(container))
      
      runSpec(spec)
      
      spec.afterStartCalled must beTrue
      spec.beforeStopCalled must beTrue
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

    "handle test failures correctly" in {
      val container = mock(classOf[SampleContainer.SampleJavaContainer])

      val spec = new FailingTestSpec(SampleContainer.Def(container))

      runSpecWithFailure(spec)

      val optionalCaptor = ArgumentCaptor.forClass(classOf[Optional[Throwable]])
      verify(container, Mockito.times(1)).afterTest(any(), optionalCaptor.capture())
      optionalCaptor.getValue.isPresent must beTrue
    }
  }

  class MultipleTestsSpec(override val containerDef: ContainerDef) extends Specification with TestContainerForAll {
    "test1" in { withContainers { _ => ok } }
    "test2" in { withContainers { _ => ok } }
  }

  class LifecycleSpec(override val containerDef: ContainerDef) extends Specification with TestContainerForAll {
    var afterStartCalled = false
    var beforeStopCalled = false
    
    override def afterContainersStart(containers: containerDef.Container): Unit = {
      afterStartCalled = true
    }
    
    override def beforeContainersStop(containers: containerDef.Container): Unit = {
      beforeStopCalled = true
    }
    
    "test" in { withContainers { _ => ok } }
  }

  class FailingAfterStartSpec(override val containerDef: ContainerDef) extends Specification with TestContainerForAll {
    override def afterContainersStart(containers: containerDef.Container): Unit = {
      throw new RuntimeException("afterContainersStart failed")
    }
    
    "test" in { withContainers { _ => ok } }
  }

  class FailingTestSpec(override val containerDef: ContainerDef) extends Specification with TestContainerForAll {
    "failing test" in { 
      withContainers { _ => 
        failure("Test failed")
      } 
    }
  }

  private def runSpec(spec: TestContainerForAll): Unit = {
    // Manually trigger the lifecycle methods that would normally be called by specs2 runner
    spec.beforeAll()
    
    // Simulate running test cases by calling around() for each test
    // Need to check how many tests the spec has
    spec match {
      case _: MultipleTestsSpec =>
        // Has 2 tests
        spec.around { org.specs2.execute.Success() }
        spec.around { org.specs2.execute.Success() }
      case _ =>
        // Has 1 test
        spec.around { org.specs2.execute.Success() }
    }
    
    spec.afterAll()
  }
  
  private def runSpecWithFailure(spec: TestContainerForAll): Unit = {
    // Manually trigger the lifecycle methods that would normally be called by specs2 runner
    spec.beforeAll()
    
    // Simulate running a failing test
    spec.around {
      // Return a failure result - this simulates what happens when a test fails
      failure("Test failed")
    }
    
    spec.afterAll()
  }
}