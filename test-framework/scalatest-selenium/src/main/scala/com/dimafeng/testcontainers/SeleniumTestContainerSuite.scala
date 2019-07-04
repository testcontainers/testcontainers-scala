package com.dimafeng.testcontainers

import java.io.File
import java.net.URL
import java.util.Optional

import org.openqa.selenium.WebDriver
import org.openqa.selenium.remote.{DesiredCapabilities, RemoteWebDriver}
import org.scalatest.Suite
import org.testcontainers.containers.BrowserWebDriverContainer
import org.testcontainers.lifecycle.TestDescription

trait SeleniumTestContainerSuite extends ForEachTestContainer {
  self: Suite =>

  def desiredCapabilities: DesiredCapabilities

  def recordingMode: (BrowserWebDriverContainer.VncRecordingMode, File) = null

  val container = SeleniumContainer(desiredCapabilities, recordingMode)

  implicit def webDriver: WebDriver = container.webDriver
}

class SeleniumContainer(desiredCapabilities: Option[DesiredCapabilities] = None,
                        recordingMode: Option[(BrowserWebDriverContainer.VncRecordingMode, File)] = None)
  extends SingleContainer[BrowserWebDriverContainer[_]] with TestLifecycleAware {
  require(desiredCapabilities.isDefined, "'desiredCapabilities' is required parameter")

  type OTCContainer = BrowserWebDriverContainer[T] forSome {type T <: BrowserWebDriverContainer[T]}
  override val container: OTCContainer = new BrowserWebDriverContainer()
  desiredCapabilities.foreach(container.withDesiredCapabilities)
  recordingMode.foreach(Function.tupled(container.withRecordingMode))

  def password: String = container.getPassword

  def port: Int = container.getPort

  def seleniumAddress: URL = container.getSeleniumAddress

  def vncAddress: String = container.getVncAddress

  def webDriver: RemoteWebDriver = container.getWebDriver

  override def afterTest(description: TestDescription, throwable: Option[Throwable]): Unit = {
    val javaThrowable: Optional[Throwable] = throwable match {
      case Some(error) => Optional.of(error)
      case None => Optional.empty()
    }
    container.afterTest(description, javaThrowable)
  }
}

object SeleniumContainer {
  def apply(desiredCapabilities: DesiredCapabilities = null, recordingMode: (BrowserWebDriverContainer.VncRecordingMode, File) = null): SeleniumContainer =
    new SeleniumContainer(Option(desiredCapabilities), Option(recordingMode))
}

