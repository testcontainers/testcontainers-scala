package com.dimafeng.testcontainers

import org.testcontainers.containers.{Neo4jContainer => JavaNeo4jContainer}

class Neo4jContainer(configurationOverride: Option[String] = None,
                     neo4jImageVersion: Option[String] = None,
                     neo4jPassword: Option[String] = None)
  extends SingleContainer[JavaNeo4jContainer[_]] {

  override val container: JavaNeo4jContainer[_] = neo4jImageVersion
    .map(new JavaNeo4jContainer(_))
    .getOrElse(new JavaNeo4jContainer(Neo4jContainer.DEFAULT_NEO4J_VERSION))

  neo4jPassword.map(container.withAdminPassword)

  def boltUrl: String = container.getBoltUrl

  def password: String = container.getAdminPassword

  def username: String = "neo4j"
}

object Neo4jContainer {
  private val DEFAULT_IMAGE_NAME = "neo4j"
  private val DEFAULT_TAG = "3.5.0"
  val defaultDockerImageName = s"${DEFAULT_IMAGE_NAME}:${DEFAULT_TAG}"
  val defaultPassword = "password"

  val DEFAULT_NEO4J_VERSION = defaultDockerImageName

  def apply(configurationOverride: String = null,
            neo4jImageVersion: String = null,
            password: String = null): Neo4jContainer =
    new Neo4jContainer(Option(configurationOverride),
      Option(neo4jImageVersion),
      Option(password))

  case class Def(
    dockerImageName: String = defaultDockerImageName,
    password: String = defaultPassword,
    configurationOverride: Option[String] = None
  ) extends ContainerDef {

    override type Container = Neo4jContainer

    override def createContainer(): Neo4jContainer = {
      new Neo4jContainer(
        neo4jImageVersion = Some(dockerImageName),
        neo4jPassword = Some(password),
        configurationOverride = configurationOverride
      )
    }
  }

}
