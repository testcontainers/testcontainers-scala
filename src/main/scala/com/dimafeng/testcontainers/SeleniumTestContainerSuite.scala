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
  private val c = new BrowserWebDriverContainer()
  desiredCapabilities.foreach { v => c.withDesiredCapabilities(v); Unit }
  recordingMode.foreach { v => c.withRecordingMode(v._1, v._2); Unit }

  override def container = c

  def password = c.getPassword

  def port = c.getPort

  def seleniumAddress = c.getSeleniumAddress

  def vncAddress = c.getVncAddress

  def webDriver = c.getWebDriver
}

object SeleniumContainer {
  def apply(desiredCapabilities: DesiredCapabilities = null, recordingMode: (BrowserWebDriverContainer.VncRecordingMode, File) = null) =
    new SeleniumContainer(Option(desiredCapabilities), Option(recordingMode))
}
