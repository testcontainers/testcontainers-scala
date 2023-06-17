package com.dimafeng.testcontainers

import org.mockito.MockitoAnnotations
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatestplus.mockito.MockitoSugar
import scala.reflect.ClassTag

abstract class BaseSpec[T: ClassTag]
  extends AnyFlatSpec with Matchers with MockitoSugar with BeforeAndAfterEach with BeforeAndAfterAll {

  behavior of implicitly[ClassTag[T]].runtimeClass.asInstanceOf[Class[T]].getSimpleName

  override def beforeEach(): Unit = MockitoAnnotations.openMocks(this)
}
