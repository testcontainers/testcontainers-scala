package com.dimafeng.testcontainers

import java.io.File
import org.openqa.selenium.WebDriver
import org.openqa.selenium.remote.DesiredCapabilities
import org.scalatest.Suite
import org.testcontainers.containers.BrowserWebDriverContainer


trait SeleniumTestContainerSuite extends ForEachTestContainer {
  self: Suite =>

  val desiredCapabilities: DesiredCapabilities = null
  val recordingMode: (BrowserWebDriverContainer.VncRecordingMode, File) = null
  var seleniumContainer = SeleniumContainer(desiredCapabilities, recordingMode)

  val container = seleniumContainer

  implicit def webDriver: WebDriver = container.webDriver
}

class SeleniumContainer(desiredCapabilities: Option[DesiredCapabilities] = None,
                        recordingMode: Option[(BrowserWebDriverContainer.VncRecordingMode, File)] = None) extends SingleContainer[BrowserWebDriverContainer[_]] {
  override  val container = new BrowserWebDriverContainer()
  desiredCapabilities.foreach { v => container.withDesiredCapabilities(v); Unit }
  recordingMode.foreach { v => container.withRecordingMode(v._1, v._2); Unit }

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
