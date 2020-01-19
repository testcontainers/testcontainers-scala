package com.dimafeng.testcontainers

import org.testcontainers.containers.{Db2Container => JavaDb2Container}

case class Db2Container(
  dockerImageName: String = Db2Container.defaultDockerImageName,
  dbName: String = Db2Container.defaultDatabaseName,
  dbUsername: String = Db2Container.defaultUsername,
  dbPassword: String = Db2Container.defaultPassword
) extends SingleContainer[JavaDb2Container] with JdbcDatabaseContainer {

  override val container: JavaDb2Container = {
    val c = new JavaDb2Container(dockerImageName)
    c.withDatabaseName(dbName)
    c.withUsername(username)
    c.withPassword(password)
    c.acceptLicense()
    c
  }
}

object Db2Container {

  val defaultDockerImageName = s"${JavaDb2Container.DEFAULT_DB2_IMAGE_NAME}:${JavaDb2Container.DEFAULT_TAG}"
  val defaultDatabaseName = "test"
  val defaultUsername = "db2inst1"
  val defaultPassword = "foobar1234"

  case class Def(
    dockerImageName: String = Db2Container.defaultDockerImageName,
    dbName: String = Db2Container.defaultDatabaseName,
    dbUsername: String = Db2Container.defaultUsername,
    dbPassword: String = Db2Container.defaultPassword
  ) extends ContainerDef {

    override type Container = Db2Container

    override def createContainer(): Db2Container = {
      new Db2Container(
        dockerImageName,
        dbName,
        dbUsername,
        dbPassword
      )
    }
  }
}
