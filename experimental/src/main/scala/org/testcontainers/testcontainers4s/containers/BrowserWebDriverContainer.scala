package org.testcontainers.testcontainers4s.containers

import java.io.File
import java.net.URL
import java.util.Optional

import org.openqa.selenium.Capabilities
import org.openqa.selenium.remote.RemoteWebDriver
import org.testcontainers.containers.BrowserWebDriverContainer.VncRecordingMode
import org.testcontainers.containers.{RecordingFileFactory, BrowserWebDriverContainer => JavaBrowserWebDriverContainer}
import org.testcontainers.lifecycle.TestDescription
import org.testcontainers.testcontainers4s.lifecycle.TestLifecycleAware

object BrowserWebDriverContainer {

  case class Def(
    dockerImageName: Option[String] = None,
    capabilities: Option[Capabilities] = None,
    recordingMode: Option[(VncRecordingMode, File)] = None,
    recordingFileFactory: Option[RecordingFileFactory] = None,
  ) extends Container {

    override type Container = BrowserWebDriverContainer

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
) extends ContainerRuntime with TestLifecycleAware {

  override type JavaContainer = JavaBrowserWebDriverContainer[_]

  override def afterTest(description: TestDescription, throwable: Option[Throwable]): Unit = {
    val javaThrowable: Optional[Throwable] = throwable match {
      case Some(error) => Optional.of(error)
      case None => Optional.empty()
    }
    underlyingUnsafeContainer.afterTest(description, javaThrowable)
  }

  def seleniumAddress: URL = underlyingUnsafeContainer.getSeleniumAddress
  def vncAddress: String = underlyingUnsafeContainer.getVncAddress
  def password: String = underlyingUnsafeContainer.getPassword
  def port: Int = underlyingUnsafeContainer.getPort
  def webDriver: RemoteWebDriver = underlyingUnsafeContainer.getWebDriver
}
