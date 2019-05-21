package com.dimafeng.testcontainers

class PostgreSQLContainer(dockerImageNameOverride: Option[String] = None) extends SingleContainer[OTCPostgreSQLContainer[_]] {

  type OTCContainer = OTCPostgreSQLContainer[T] forSome {type T <: OTCPostgreSQLContainer[T]}

  override val container: OTCContainer = dockerImageNameOverride match {

    case Some(imageNameOverride) =>
      new OTCPostgreSQLContainer(imageNameOverride)

    case None =>
      new OTCPostgreSQLContainer()
  }

  def driverClassName: String = container.getDriverClassName

  def jdbcUrl: String = container.getJdbcUrl

  def username: String = container.getUsername

  def password: String = container.getPassword

  def testQueryString: String = container.getTestQueryString
}

object PostgreSQLContainer {
  def apply(dockerImageNameOverride: String = null): PostgreSQLContainer = new PostgreSQLContainer(Option(dockerImageNameOverride))
}
