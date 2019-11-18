package com.dimafeng.testcontainers

import org.testcontainers.containers.{PostgreSQLContainer => JavaPostgreSQLContainer}

class PostgreSQLContainer(dockerImageNameOverride: Option[String] = None,
                          databaseName: Option[String] = None,
                          pgUsername: Option[String] = None,
                          pgPassword: Option[String] = None,
                          mountPostgresDataToTmpfs: Boolean = false) extends SingleContainer[JavaPostgreSQLContainer[_]] {

  override val container: JavaPostgreSQLContainer[_] = dockerImageNameOverride match {

    case Some(imageNameOverride) =>
      new JavaPostgreSQLContainer(imageNameOverride)

    case None =>
      new JavaPostgreSQLContainer()
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

  val defaultDockerImageName = s"${JavaPostgreSQLContainer.IMAGE}:${JavaPostgreSQLContainer.DEFAULT_TAG}"
  val defaultDatabaseName = "test"
  val defaultUsername = "test"
  val defaultPassword = "test"

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

  case class Def(
    dockerImageName: String = defaultDockerImageName,
    databaseName: String = defaultDatabaseName,
    username: String = defaultUsername,
    password: String = defaultPassword,
    mountPostgresDataToTmpfs: Boolean = false
  ) extends ContainerDef {

    override type Container = PostgreSQLContainer

    override def createContainer(): PostgreSQLContainer = {
      new PostgreSQLContainer(
        dockerImageNameOverride = Some(dockerImageName),
        databaseName = Some(databaseName),
        pgUsername = Some(username),
        pgPassword = Some(password),
        mountPostgresDataToTmpfs = mountPostgresDataToTmpfs
      )
    }
  }
}
