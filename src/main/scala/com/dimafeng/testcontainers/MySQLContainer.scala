package com.dimafeng.testcontainers

import java.sql.Driver

import org.testcontainers.containers.{MySQLContainer => OTCMySQLContainer}

class MySQLContainer(configurationOverride: Option[String] = None) extends SingleContainer[OTCMySQLContainer[_]] {

  type OTCContainer = OTCMySQLContainer[T] forSome {type T <: OTCMySQLContainer[T]}
  override val container: OTCContainer = new OTCMySQLContainer() {
    var driver: Driver = null

    override def optionallyMapResourceParameterAsVolume(paramName: String, pathNameInContainer: String, defaultResource: String): Unit = {
      // Workaround for unconditional overridden config application "Could not locate a classpath resource for TC_MY_CNF of mysql-default-conf"
      if (parameters.containsKey(paramName)) {
        super.optionallyMapResourceParameterAsVolume(paramName, pathNameInContainer, defaultResource)
      }
    }

    // Fix for http://stackoverflow.com/questions/26579108/custom-classloader-fails-after-upgrading-sbt-from-0-12-2-to-0-13
    override def getJdbcDriverInstance: Driver = {
      synchronized {
        if (driver == null) try
          driver = this.getClass.getClassLoader.loadClass(this.getDriverClassName).newInstance.asInstanceOf[Driver]

        catch {
          case e: Any => {
            throw new RuntimeException("Could not get Driver", e)
          }
        }
      }
      driver
    }
  }
  configurationOverride.foreach(container.withConfigurationOverride)

  def driverClassName: String = container.getDriverClassName

  def jdbcUrl: String = container.getJdbcUrl

  def password: String = container.getPassword

  def testQueryString: String = container.getTestQueryString

  def username: String = container.getUsername
}

object MySQLContainer {
  def apply(configurationOverride: String = null) = new MySQLContainer(Option(configurationOverride))
}