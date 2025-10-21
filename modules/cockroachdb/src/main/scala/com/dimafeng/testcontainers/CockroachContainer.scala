package com.dimafeng.testcontainers

import org.testcontainers.cockroachdb.{CockroachContainer => JavaCockroachContainer}
import org.testcontainers.utility.DockerImageName

case class CockroachContainer(
  dockerImageName: DockerImageName = DockerImageName.parse(CockroachContainer.defaultDockerImageName),
  urlParams: Map[String, String] = Map.empty,
  commonJdbcParams: JdbcDatabaseContainer.CommonParams = JdbcDatabaseContainer.CommonParams()
) extends SingleContainer[JavaCockroachContainer] with JdbcDatabaseContainer {

  override val container: JavaCockroachContainer = {
    val c = new JavaCockroachContainer(dockerImageName)

    urlParams.foreach { case (key, value) =>
      c.withUrlParam(key, value)
    }

    commonJdbcParams.applyTo(c)

    c
  }

  def testQueryString: String = container.getTestQueryString
}

object CockroachContainer {

  val defaultDockerImageName = "cockroachdb/cockroach"

  case class Def(
    dockerImageName: DockerImageName = DockerImageName.parse(CockroachContainer.defaultDockerImageName),
    urlParams: Map[String, String] = Map.empty,
    commonJdbcParams: JdbcDatabaseContainer.CommonParams = JdbcDatabaseContainer.CommonParams()
  ) extends ContainerDef {

    override type Container = CockroachContainer

    override def createContainer(): CockroachContainer = {
      new CockroachContainer(
        dockerImageName,
        urlParams,
        commonJdbcParams
      )
    }
  }
}
