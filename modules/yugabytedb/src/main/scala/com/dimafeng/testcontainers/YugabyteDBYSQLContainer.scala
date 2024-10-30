package com.dimafeng.testcontainers

import org.testcontainers.containers.{YugabyteDBYSQLContainer => JavaYugabyteYSQLContainer}
import org.testcontainers.utility.DockerImageName

class YugabyteDBYSQLContainer(
    underlying: JavaYugabyteYSQLContainer
) extends SingleContainer[JavaYugabyteYSQLContainer] {

  override val container: JavaYugabyteYSQLContainer = underlying

  def databaseName: String = container.getDatabaseName

  def username: String = container.getUsername

  def password: String = container.getPassword

  def driverClassName: String = container.getDriverClassName

  def jdbcUrl: String = container.getJdbcUrl

  def testQueryString: String = container.getTestQueryString

}

object YugabyteDBYSQLContainer {

  val defaultImage           = "yugabytedb/yugabyte"
  val defaultTag             = "2.20.7.1-b10"
  val defaultDockerImageName = s"$defaultImage:$defaultTag"

  val ysqlPort    = 5433
  val masterDashboardPort = 7000
  val tserverDashboardPort = 9000

  case class Def(
      dockerImageName: DockerImageName = DockerImageName.parse(YugabyteDBYSQLContainer.defaultDockerImageName),
      private val builder: List[JavaYugabyteYSQLContainer => JavaYugabyteYSQLContainer] = List.empty
  ) extends ContainerDef {
    override type Container = YugabyteDBYSQLContainer

    def withDatabaseName(database: String): Def =
      copy(builder = ((_: JavaYugabyteYSQLContainer).withDatabaseName(database)) :: builder)

    def withUsername(username: String): Def =
      copy(builder = ((_: JavaYugabyteYSQLContainer).withUsername(username)) :: builder)

    def withPassword(password: String): Def =
      copy(builder = ((_: JavaYugabyteYSQLContainer).withPassword(password)) :: builder)

    override def createContainer(): YugabyteDBYSQLContainer = {
      new YugabyteDBYSQLContainer(
        builder
          .foldRight(new JavaYugabyteYSQLContainer(dockerImageName))((f, underlying) => f(underlying))
      )
    }
  }
}
