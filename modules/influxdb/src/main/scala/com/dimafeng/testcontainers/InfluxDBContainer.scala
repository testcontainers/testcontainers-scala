package com.dimafeng.testcontainers

import org.influxdb.InfluxDB
import org.testcontainers.containers.{InfluxDBContainer => JavaInfluxDBContainer}
import org.testcontainers.utility.DockerImageName

case class InfluxDBContainer(
  dockerImageName: DockerImageName,
  database: String,
  admin: String,
  adminPassword: String,
  username: String,
  password: String,
  authEnabled: Boolean
) extends SingleContainer[JavaInfluxDBContainer[_]] {

  @deprecated("Use `DockerImageName` for `dockerImageName` instead")
  def this(
    tag: String = InfluxDBContainer.defaultTag,
    database: String = InfluxDBContainer.defaultDatabase,
    admin: String = InfluxDBContainer.defaultAdmin,
    adminPassword: String = InfluxDBContainer.defaultAdminPassword,
    username: String = InfluxDBContainer.defaultUsername,
    password: String = InfluxDBContainer.defaultPassword,
    authEnabled: Boolean = InfluxDBContainer.defaultAuthEnabled
  ) {
    this(
      DockerImageName.parse(InfluxDBContainer.defaultImage).withTag(tag),
      database,
      admin,
      adminPassword,
      username,
      password,
      authEnabled
    )
  }

  override val container: JavaInfluxDBContainer[_] = {
    val c: JavaInfluxDBContainer[_] = new JavaInfluxDBContainer(dockerImageName)
    c.withDatabase(database)
    c.withAdmin(admin)
    c.withAdminPassword(adminPassword)
    c.withUsername(username)
    c.withPassword(password)
    c.withAuthEnabled(authEnabled)
    c
  }

  def newInfluxDB: InfluxDB = container.getNewInfluxDB

  @deprecated("Use `dockerImageName.getVersionPart` instead")
  def tag: String = dockerImageName.getVersionPart

  def url: String = container.getUrl
}

object InfluxDBContainer {

  val defaultImage = "influxdb"
  val defaultTag = JavaInfluxDBContainer.VERSION
  val defaultDockerImageName = s"$defaultImage:$defaultTag"
  val defaultDatabase = "test"
  val defaultAdmin = "admin"
  val defaultAdminPassword = "password"
  val defaultUsername = "any"
  val defaultPassword = "any"
  val defaultAuthEnabled = true

  case class Def(
    dockerImageName: DockerImageName = DockerImageName.parse(InfluxDBContainer.defaultDockerImageName),
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
        dockerImageName,
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
