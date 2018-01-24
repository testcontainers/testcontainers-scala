package com.dimafeng.testcontainers

import com.dimafeng.testcontainers.ContainerSpec.{SampleContainer, SampleOTCContainer, TestSpec}
import org.mockito.Matchers.any
import org.mockito.Mockito.{times, verify}
import org.scalatest.{Args, Reporter}

class MultipleContainersSpec extends BaseSpec[ForEachTestContainer] {

  behavior of "Multiple Containers inside suite"

  it should "call all expected methods of the multiple containers" in {
    val container1 = mock[SampleOTCContainer]
    val container2 = mock[SampleOTCContainer]

    new TestSpec({
      assert(1 == 1)
    }) {
      new SampleContainer(container1)
      new SampleContainer(container2)
    }.run(None, Args(mock[Reporter]))

    verify(container1).starting(any())
    verify(container1, times(0)).failed(any(), any())
    verify(container1).finished(any())
    verify(container1).succeeded(any())
    verify(container2).starting(any())
    verify(container2, times(0)).failed(any(), any())
    verify(container2).finished(any())
    verify(container2).succeeded(any())
  }
}
