package com.dimafeng.testcontainers

import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, FlatSpec, Matchers}
import org.scalatest.junit.JUnitRunner
import org.scalatest.mockito.MockitoSugar

@RunWith(classOf[JUnitRunner])
abstract class BaseSpec[T: Manifest]
  extends FlatSpec with Matchers with MockitoSugar with BeforeAndAfterEach with BeforeAndAfterAll {

  behavior of implicitly[Manifest[T]].runtimeClass.asInstanceOf[Class[T]].getSimpleName

  override def beforeEach(): Unit = MockitoAnnotations.initMocks(this)
}
