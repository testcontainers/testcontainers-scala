package com.dimafeng.testcontainers.integration

import com.dimafeng.testcontainers.SeleniumTestContainerSuite
import org.openqa.selenium.remote.DesiredCapabilities
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatestplus.selenium.WebBrowser

class SeleniumSpec extends AnyFlatSpec with SeleniumTestContainerSuite with WebBrowser {
  override def desiredCapabilities = DesiredCapabilities.chrome()

  "Browser" should "show google" in {
    go to "http://google.com"
  }
}
