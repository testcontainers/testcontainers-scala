package com.dimafeng.testcontainers.integration

import com.dimafeng.testcontainers.SeleniumTestContainerSuite
import org.junit.runner.RunWith
import org.openqa.selenium.remote.DesiredCapabilities
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner
import org.scalatest.selenium.WebBrowser

class SeleniumSpec extends FlatSpec with SeleniumTestContainerSuite with WebBrowser {
  override def desiredCapabilities = DesiredCapabilities.chrome()

  "Browser" should "show google" in {
    go to "http://google.com"
  }
}
