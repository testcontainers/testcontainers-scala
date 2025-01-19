package com.dimafeng.testcontainers

import org.testcontainers.containers.{PostgreSQLContainer => JavaPostgreSQLContainer}
import org.testcontainers.utility.DockerImageName

class PostgreSQLContainer(
  dockerImageNameOverride: Option[DockerImageName] = None,
  databaseName: Option[String] = None,
  pgUsername: Option[String] = None,
  pgPassword: Option[String] = None,
  mountPostgresDataToTmpfs: Boolean = false,
  urlParams: Map[String, String] = Map.empty,
  commonJdbcParams: JdbcDatabaseContainer.CommonParams = JdbcDatabaseContainer.CommonParams()
) extends SingleContainer[JavaPostgreSQLContainer[?]] with JdbcDatabaseContainer {

  import PostgreSQLContainer._

  override val container: JavaPostgreSQLContainer[?] = {
    val dockerImageName = dockerImageNameOverride.getOrElse(parsedDockerImageName)
    val c: JavaPostgreSQLContainer[?] = new JavaPostgreSQLContainer(dockerImageName)

    databaseName.foreach(c.withDatabaseName)
    pgUsername.foreach(c.withUsername)
    pgPassword.foreach(c.withPassword)

    // as suggested in https://github.com/testcontainers/testcontainers-java/issues/1256
    // mounting the postgres data directory to an in-memory docker volume (https://docs.docker.com/storage/tmpfs/)
    // can improve performance
    if (mountPostgresDataToTmpfs){
      val tmpfsMount = new java.util.HashMap[String, String]()
      tmpfsMount.put("/var/lib/postgresql/data", "rw")
      c.withTmpFs(tmpfsMount)
    }

    urlParams.foreach { case (key, value) =>
      c.withUrlParam(key, value)
    }

    commonJdbcParams.applyTo(c)

    c
  }

  def testQueryString: String = container.getTestQueryString
}

object PostgreSQLContainer {

  val defaultDockerImageName = s"${JavaPostgreSQLContainer.IMAGE}:${JavaPostgreSQLContainer.DEFAULT_TAG}"
  val defaultDatabaseName = "test"
  val defaultUsername = "test"
  val defaultPassword = "test"

  private[testcontainers] def parsedDockerImageName: DockerImageName =
    DockerImageName.parse(defaultDockerImageName)

  def apply(
    dockerImageNameOverride: DockerImageName = null,
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
    dockerImageName: DockerImageName = parsedDockerImageName,
    databaseName: String = defaultDatabaseName,
    username: String = defaultUsername,
    password: String = defaultPassword,
    mountPostgresDataToTmpfs: Boolean = false,
    urlParams: Map[String, String] = Map.empty,
    commonJdbcParams: JdbcDatabaseContainer.CommonParams = JdbcDatabaseContainer.CommonParams()
  ) extends ContainerDef {

    override type Container = PostgreSQLContainer

    override def createContainer(): PostgreSQLContainer = {
      new PostgreSQLContainer(
        dockerImageNameOverride = Some(dockerImageName),
        databaseName = Some(databaseName),
        pgUsername = Some(username),
        pgPassword = Some(password),
        mountPostgresDataToTmpfs = mountPostgresDataToTmpfs,
        urlParams = urlParams,
        commonJdbcParams = commonJdbcParams
      )
    }
  }
}
