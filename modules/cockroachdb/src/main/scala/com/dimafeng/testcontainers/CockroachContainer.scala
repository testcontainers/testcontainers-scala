package com.dimafeng.testcontainers

import org.testcontainers.containers.{CockroachContainer => JavaCockroachContainer}

case class CockroachContainer(
  dockerImageName: String = CockroachContainer.defaultDockerImageName
) extends SingleContainer[JavaCockroachContainer] with JdbcDatabaseContainer {

  override val container: JavaCockroachContainer = new JavaCockroachContainer(dockerImageName)

  def testQueryString: String = container.getTestQueryString
}

object CockroachContainer {

  val defaultDockerImageName = s"${JavaCockroachContainer.IMAGE}:${JavaCockroachContainer.IMAGE_TAG}"

  case class Def(
    dockerImageName: String = CockroachContainer.defaultDockerImageName
  ) extends ContainerDef {

    override type Container = CockroachContainer

    override def createContainer(): CockroachContainer = {
      new CockroachContainer(
        dockerImageName = dockerImageName
      )
    }
  }
}
