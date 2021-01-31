package com.dimafeng.testcontainers

import org.mockito.MockitoAnnotations
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatestplus.mockito.MockitoSugar

abstract class BaseSpec[T: Manifest]
  extends AnyFlatSpec with Matchers with MockitoSugar with BeforeAndAfterEach with BeforeAndAfterAll {

  behavior of implicitly[Manifest[T]].runtimeClass.asInstanceOf[Class[T]].getSimpleName

  override def beforeEach(): Unit = MockitoAnnotations.initMocks(this)
}
