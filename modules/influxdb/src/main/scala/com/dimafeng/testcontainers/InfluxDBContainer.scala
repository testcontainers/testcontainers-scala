package com.dimafeng.testcontainers

import org.influxdb.InfluxDB
import org.testcontainers.containers.{InfluxDBContainer => JavaInfluxDBContainer}

case class InfluxDBContainer(
  tag: String = InfluxDBContainer.defaultTag,
  database: String = InfluxDBContainer.defaultDatabase,
  admin: String = InfluxDBContainer.defaultAdmin,
  adminPassword: String = InfluxDBContainer.defaultAdminPassword,
  username: String = InfluxDBContainer.defaultUsername,
  password: String = InfluxDBContainer.defaultPassword,
  authEnabled: Boolean = InfluxDBContainer.defaultAuthEnabled
) extends SingleContainer[JavaInfluxDBContainer[?]] {

  override val container: JavaInfluxDBContainer[?] = {
    val c: JavaInfluxDBContainer[?] = new JavaInfluxDBContainer(tag)
    c.withDatabase(database)
    c.withAdmin(admin)
    c.withAdminPassword(adminPassword)
    c.withUsername(username)
    c.withPassword(password)
    c.withAuthEnabled(authEnabled)
    c
  }

  def newInfluxDB: InfluxDB = container.getNewInfluxDB

  def url: String = container.getUrl
}

object InfluxDBContainer {

  val defaultTag = JavaInfluxDBContainer.VERSION
  val defaultDatabase = "test"
  val defaultAdmin = "admin"
  val defaultAdminPassword = "password"
  val defaultUsername = "any"
  val defaultPassword = "any"
  val defaultAuthEnabled = true

  case class Def(
    tag: String = InfluxDBContainer.defaultTag,
    database: String = InfluxDBContainer.defaultDatabase,
    admin: String = InfluxDBContainer.defaultAdmin,
    adminPassword: String = InfluxDBContainer.defaultAdminPassword,
    username: String = InfluxDBContainer.defaultUsername,
    password: String = InfluxDBContainer.defaultPassword,
    authEnabled: Boolean = InfluxDBContainer.defaultAuthEnabled
  ) extends ContainerDef {

    override type Container = InfluxDBContainer

    override def createContainer(): InfluxDBContainer = {
      new InfluxDBContainer(
        tag,
        database,
        admin,
        adminPassword,
        username,
        password,
        authEnabled
      )
    }
  }
}