package org.testcontainers.testcontainers4s.containers

import java.io.File
import java.net.URL

import org.openqa.selenium.Capabilities
import org.openqa.selenium.remote.RemoteWebDriver
import org.testcontainers.containers.BrowserWebDriverContainer.VncRecordingMode
import org.testcontainers.containers.{RecordingFileFactory, BrowserWebDriverContainer => JavaBrowserWebDriverContainer}

object BrowserWebDriverContainer {

  class Def(
    dockerImageName: Option[String] = None,
    capabilities: Option[Capabilities] = None,
    recordingMode: Option[(VncRecordingMode, File)] = None,
    recordingFileFactory: Option[RecordingFileFactory] = None,
  ) extends ContainerDef[JavaBrowserWebDriverContainer[_], BrowserWebDriverContainer] {

    override def createContainer(): BrowserWebDriverContainer = {
      val javaContainer = dockerImageName match {
        case Some(imageName) => new JavaBrowserWebDriverContainer(imageName)
        case None => new JavaBrowserWebDriverContainer()
      }
      capabilities.foreach(javaContainer.withCapabilities)
      recordingMode.foreach { case (mode, file) =>
        javaContainer.withRecordingMode(mode, file)
      }
      recordingFileFactory.foreach(javaContainer.withRecordingFileFactory)

      new BrowserWebDriverContainer(javaContainer)
    }
  }
}

class BrowserWebDriverContainer private[containers] (
  val underlyingUnsafeContainer: JavaBrowserWebDriverContainer[_]
) extends Container[JavaBrowserWebDriverContainer[_]] {

  def seleniumAddress: URL = underlyingUnsafeContainer.getSeleniumAddress
  def vncAddress: String = underlyingUnsafeContainer.getVncAddress
  def password: String = underlyingUnsafeContainer.getPassword
  def port: Int = underlyingUnsafeContainer.getPort
  def webDriver: RemoteWebDriver = underlyingUnsafeContainer.getWebDriver
}
