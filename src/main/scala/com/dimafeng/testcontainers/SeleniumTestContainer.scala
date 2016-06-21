package com.dimafeng.testcontainers

import java.io.File

import org.openqa.selenium.WebDriver
import org.openqa.selenium.remote.DesiredCapabilities
import org.scalatest.Suite
import org.testcontainers.containers.BrowserWebDriverContainer


trait SeleniumTestContainer extends ForEachTestContainer {
  self: Suite =>

  val webDriverContainer = new BrowserWebDriverContainer
  val container = Container(webDriverContainer)
  implicit def webDriver: WebDriver = webDriverContainer.getWebDriver

  def withDesiredCapabilities(desiredCapabilities: DesiredCapabilities): Unit = {
    webDriverContainer.withDesiredCapabilities(desiredCapabilities)
  }

  def withRecordingMode(recordingMode: BrowserWebDriverContainer.VncRecordingMode, vncRecordingDirectory: File): Unit = {
    webDriverContainer.withRecordingMode(recordingMode, vncRecordingDirectory)
  }
}
