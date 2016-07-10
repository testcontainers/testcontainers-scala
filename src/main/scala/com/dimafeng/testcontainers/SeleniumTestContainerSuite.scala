package com.dimafeng.testcontainers

import java.io.File

import org.openqa.selenium.WebDriver
import org.openqa.selenium.remote.DesiredCapabilities
import org.scalatest.Suite
import org.testcontainers.containers.BrowserWebDriverContainer


trait SeleniumTestContainerSuite extends ForEachTestContainer {
  self: Suite =>

  def desiredCapabilities: DesiredCapabilities
  def recordingMode: (BrowserWebDriverContainer.VncRecordingMode, File) = null

  val container  = SeleniumContainer(desiredCapabilities, recordingMode)

  implicit def webDriver: WebDriver = container.webDriver
}

class SeleniumContainer(desiredCapabilities: Option[DesiredCapabilities] = None,
                        recordingMode: Option[(BrowserWebDriverContainer.VncRecordingMode, File)] = None) extends SingleContainer[BrowserWebDriverContainer[_]] {
  require(desiredCapabilities.isDefined, "'desiredCapabilities' is required parameter")

  type OTCContainer = BrowserWebDriverContainer[T] forSome {type T <: BrowserWebDriverContainer[T]}
  override val container: OTCContainer = new BrowserWebDriverContainer()
  desiredCapabilities.foreach(container.withDesiredCapabilities)
  recordingMode.foreach(Function.tupled(container.withRecordingMode))

  def password = container.getPassword

  def port = container.getPort

  def seleniumAddress = container.getSeleniumAddress

  def vncAddress = container.getVncAddress

  def webDriver = container.getWebDriver
}

object SeleniumContainer {
  def apply(desiredCapabilities: DesiredCapabilities = null, recordingMode: (BrowserWebDriverContainer.VncRecordingMode, File) = null) =
    new SeleniumContainer(Option(desiredCapabilities), Option(recordingMode))
}
