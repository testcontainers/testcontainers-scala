package com.dimafeng.testcontainers.integration

import com.dimafeng.testcontainers.SeleniumTestContainerSuite
import org.junit.runner.RunWith
import org.openqa.selenium.remote.DesiredCapabilities
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner
import org.scalatest.selenium.WebBrowser

@RunWith(classOf[JUnitRunner])
class SeleniumSpec extends FlatSpec with SeleniumTestContainerSuite with WebBrowser {
  override val desiredCapabilities = DesiredCapabilities.chrome()

  "Browser" should "show google" in {
    go to "http://google.com"
  }
}
