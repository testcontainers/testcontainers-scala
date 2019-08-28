package com.dimafeng.testcontainers

import org.testcontainers.containers.{PostgreSQLContainer => OTCPostgreSQLContainer}

class PostgreSQLContainer(dockerImageNameOverride: Option[String] = None,
                          databaseName: Option[String] = None,
                          pgUsername: Option[String] = None,
                          pgPassword: Option[String] = None,
                          mountPostgresDataToTmpfs: Boolean = false) extends SingleContainer[OTCPostgreSQLContainer[_]] {

  type OTCContainer = OTCPostgreSQLContainer[T] forSome {type T <: OTCPostgreSQLContainer[T]}

  override val container: OTCContainer = dockerImageNameOverride match {

    case Some(imageNameOverride) =>
      new OTCPostgreSQLContainer(imageNameOverride)

    case None =>
      new OTCPostgreSQLContainer()
  }

  databaseName.map(container.withDatabaseName)
  pgUsername.map(container.withUsername)
  pgPassword.map(container.withPassword)

  // as suggested in https://github.com/testcontainers/testcontainers-java/issues/1256
  // mounting the postgres data directory to an in-memory docker volume (https://docs.docker.com/storage/tmpfs/)
  // can improve performance
  if (mountPostgresDataToTmpfs){
    val tmpfsMount = new java.util.HashMap[String, String]()
    tmpfsMount.put("/var/lib/postgresql/data", "rw")
    container.withTmpFs(tmpfsMount)
  }

  def driverClassName: String = container.getDriverClassName

  def jdbcUrl: String = container.getJdbcUrl

  def username: String = container.getUsername

  def password: String = container.getPassword

  def testQueryString: String = container.getTestQueryString
}

object PostgreSQLContainer {
  def apply(dockerImageNameOverride: String = null,
            databaseName: String = null,
            username: String = null,
            password: String = null,
            mountPostgresDataToTmpfs: Boolean = false
           ): PostgreSQLContainer =
    new PostgreSQLContainer(
      Option(dockerImageNameOverride),
      Option(databaseName),
      Option(username),
      Option(password),
      mountPostgresDataToTmpfs
    )
}
