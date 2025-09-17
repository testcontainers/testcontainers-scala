package com.dimafeng.testcontainers.specs2

import com.dimafeng.testcontainers.lifecycle.and
import org.specs2.mutable.Specification
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.{mock, verify, inOrder}
import org.mockito.ArgumentCaptor
import java.util.Optional
import org.specs2.specification.core.SpecificationStructure

class TestContainersForEachSpec extends Specification {
  sequential

  "TestContainersForEach" should {
    "start and stop multiple containers for each test" in {
      val container1 = mock(classOf[SampleContainer.SampleJavaContainer])
      val container2 = mock(classOf[SampleContainer.SampleJavaContainer])

      val spec = new MultipleContainersSpec(container1, container2)

      runSpec(spec)

      verify(container1, Mockito.times(2)).start()
      verify(container2, Mockito.times(2)).start()
      verify(container1, Mockito.times(2)).beforeTest(any())
      verify(container2, Mockito.times(2)).beforeTest(any())
      verify(container1, Mockito.times(2)).afterTest(any(), any())
      verify(container2, Mockito.times(2)).afterTest(any(), any())
      verify(container1, Mockito.times(2)).stop()
      verify(container2, Mockito.times(2)).stop()
      ok
    }

    "call afterContainersStart and beforeContainersStop for each test" in {
      val container1 = mock(classOf[SampleContainer.SampleJavaContainer])
      val container2 = mock(classOf[SampleContainer.SampleJavaContainer])
      
      val spec = new LifecycleSpec(container1, container2)
      
      runSpec(spec)
      
      spec.afterStartCount must equalTo(2)
      spec.beforeStopCount must equalTo(2)
    }

    "stop containers in reverse order for each test" in {
      val container1 = mock(classOf[SampleContainer.SampleJavaContainer])
      val container2 = mock(classOf[SampleContainer.SampleJavaContainer])
      
      val spec = new SingleTestSpec(container1, container2)
      
      runSpec(spec)
      
      val inOrderVerifier = inOrder(container1, container2)
      inOrderVerifier.verify(container1).start()
      inOrderVerifier.verify(container2).start()
      inOrderVerifier.verify(container2).stop()
      inOrderVerifier.verify(container1).stop()
      ok
    }

    "stop containers even when test fails" in {
      val container1 = mock(classOf[SampleContainer.SampleJavaContainer])
      val container2 = mock(classOf[SampleContainer.SampleJavaContainer])

      val spec = new FailingTestSpec(container1, container2)

      runSpec(spec)

      verify(container1, Mockito.times(1)).start()
      verify(container2, Mockito.times(1)).start()
      verify(container1, Mockito.times(1)).stop()
      verify(container2, Mockito.times(1)).stop()
      
      val optionalCaptor = ArgumentCaptor.forClass(classOf[Optional[Throwable]])
      verify(container1, Mockito.times(1)).afterTest(any(), optionalCaptor.capture())
      optionalCaptor.getValue.isPresent must beTrue
    }

    "access containers via pattern matching" in {
      val container1 = mock(classOf[SampleContainer.SampleJavaContainer])
      val container2 = mock(classOf[SampleContainer.SampleJavaContainer])
      
      val spec = new PatternMatchingSpec(container1, container2)
      
      runSpec(spec)
      
      spec.container1Accessed must beTrue
      spec.container2Accessed must beTrue
    }

    "withContainers should fail before containers are started" in {
      val container1 = mock(classOf[SampleContainer.SampleJavaContainer])
      val container2 = mock(classOf[SampleContainer.SampleJavaContainer])
      val spec = new WithContainersBeforeStartSpec(container1, container2)
      
      spec.withContainers(_ => ()) must throwAn[IllegalStateException](
        message = "'withContainers' method can't be used before all containers are started"
      )
    }
  }

  class MultipleContainersSpec(
    sampleJavaContainer1: SampleContainer.SampleJavaContainer,
    sampleJavaContainer2: SampleContainer.SampleJavaContainer
  ) extends Specification with TestContainersForEach {
    override type Containers = SampleContainer and SampleContainer

    override def startContainers(): Containers = {
      val container1 = SampleContainer.Def(sampleJavaContainer1).start()
      val container2 = SampleContainer.Def(sampleJavaContainer2).start()
      container1 and container2
    }

    "test1" in { withContainers { _ => ok } }
    "test2" in { withContainers { _ => ok } }
  }

  class SingleTestSpec(
    sampleJavaContainer1: SampleContainer.SampleJavaContainer,
    sampleJavaContainer2: SampleContainer.SampleJavaContainer
  ) extends Specification with TestContainersForEach {
    override type Containers = SampleContainer and SampleContainer

    override def startContainers(): Containers = {
      val container1 = SampleContainer.Def(sampleJavaContainer1).start()
      val container2 = SampleContainer.Def(sampleJavaContainer2).start()
      container1 and container2
    }

    "test" in { withContainers { _ => ok } }
  }

  class LifecycleSpec(
    sampleJavaContainer1: SampleContainer.SampleJavaContainer,
    sampleJavaContainer2: SampleContainer.SampleJavaContainer
  ) extends Specification with TestContainersForEach {
    override type Containers = SampleContainer and SampleContainer
    
    var afterStartCount = 0
    var beforeStopCount = 0
    
    override def startContainers(): Containers = {
      val container1 = SampleContainer.Def(sampleJavaContainer1).start()
      val container2 = SampleContainer.Def(sampleJavaContainer2).start()
      container1 and container2
    }
    
    override def afterContainersStart(containers: Containers): Unit = {
      afterStartCount += 1
    }
    
    override def beforeContainersStop(containers: Containers): Unit = {
      beforeStopCount += 1
    }
    
    "test1" in { withContainers { _ => ok } }
    "test2" in { withContainers { _ => ok } }
  }

  class FailingTestSpec(
    sampleJavaContainer1: SampleContainer.SampleJavaContainer,
    sampleJavaContainer2: SampleContainer.SampleJavaContainer
  ) extends Specification with TestContainersForEach {
    override type Containers = SampleContainer and SampleContainer
    
    override def startContainers(): Containers = {
      val container1 = SampleContainer.Def(sampleJavaContainer1).start()
      val container2 = SampleContainer.Def(sampleJavaContainer2).start()
      container1 and container2
    }
    
    "failing test" in { 
      withContainers { _ => 
        failure("Test failed")
      } 
    }
  }

  class PatternMatchingSpec(
    val sampleJavaContainer1: SampleContainer.SampleJavaContainer,
    val sampleJavaContainer2: SampleContainer.SampleJavaContainer
  ) extends Specification with TestContainersForEach {
    override type Containers = SampleContainer and SampleContainer
    
    var container1Accessed = false
    var container2Accessed = false
    
    override def startContainers(): Containers = {
      val container1 = SampleContainer.Def(sampleJavaContainer1).start()
      val container2 = SampleContainer.Def(sampleJavaContainer2).start()
      container1 and container2
    }
    
    "access containers" in {
      withContainers { case c1 and c2 =>
        container1Accessed = c1.sampleJavaContainer == sampleJavaContainer1
        container2Accessed = c2.sampleJavaContainer == sampleJavaContainer2
        ok
      }
    }
  }

  class WithContainersBeforeStartSpec(
    sampleJavaContainer1: SampleContainer.SampleJavaContainer,
    sampleJavaContainer2: SampleContainer.SampleJavaContainer
  ) extends Specification with TestContainersForEach {
    override type Containers = SampleContainer and SampleContainer
    
    override def startContainers(): Containers = {
      val container1 = SampleContainer.Def(sampleJavaContainer1).start()
      val container2 = SampleContainer.Def(sampleJavaContainer2).start()
      container1 and container2
    }
    
    // Empty spec to test withContainers before start
  }

  private def runSpec(spec: TestContainersForEach): Unit = {
    // Simulate running test cases
    spec match {
      case s: MultipleContainersSpec =>
        // Simulate running 2 tests
        runSingleTest(s)
        runSingleTest(s)
      case s: LifecycleSpec =>
        // Simulate running 2 tests
        runSingleTest(s)
        runSingleTest(s)
      case s: SingleTestSpec =>
        // Simulate running 1 test
        runSingleTest(s)
      case s: FailingTestSpec =>
        // Simulate running 1 failing test
        runSingleTest(s)
      case s: PatternMatchingSpec =>
        // Simulate running 1 test with pattern matching
        s.around {
          s.withContainers { case c1 and c2 =>
            s.container1Accessed = c1.sampleJavaContainer == s.sampleJavaContainer1
            s.container2Accessed = c2.sampleJavaContainer == s.sampleJavaContainer2
            org.specs2.execute.Success()
          }
        }
    }
  }
  
  private def runSingleTest(spec: TestContainersForEach): Unit = {
    // Note: beforeTest and afterTest are called within the around method implementation
    spec.around {
      spec match {
        case _: FailingTestSpec => failure("Test failed")
        case _ => org.specs2.execute.Success()
      }
    }
  }
}