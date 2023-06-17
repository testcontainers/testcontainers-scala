package com.dimafeng.testcontainers.integration

import com.dimafeng.testcontainers.SeleniumTestContainerSuite
import org.openqa.selenium.Platform
import org.openqa.selenium.remote.DesiredCapabilities
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatestplus.selenium.WebBrowser

class SeleniumSpec extends AnyFlatSpec with SeleniumTestContainerSuite with WebBrowser {
  override def desiredCapabilities: DesiredCapabilities = new DesiredCapabilities("chrome", "1.0", Platform.LINUX)

  "Browser" should "show google" in {
    go to "http://google.com"
  }
}
