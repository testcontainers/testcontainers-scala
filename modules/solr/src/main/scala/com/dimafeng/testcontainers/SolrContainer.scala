package com.dimafeng.testcontainers

import java.net.URL

import org.testcontainers.containers.{SolrContainerConfiguration, SolrContainer => JavaSolrContainer}

case class SolrContainer(
  dockerImageName: String = SolrContainer.defaultDockerImageName,
  zookeeper: Boolean = SolrContainer.defaultConfig.isZookeeper,
  collectionName: String = SolrContainer.defaultConfig.getCollectionName,
  configurationName: String = SolrContainer.defaultConfig.getConfigurationName,
  configuration: URL = SolrContainer.defaultConfig.getSolrConfiguration,
  schema: URL = SolrContainer.defaultConfig.getSolrSchema
) extends SingleContainer[JavaSolrContainer] {

  override val container: JavaSolrContainer = {
    val c = new JavaSolrContainer(dockerImageName)

    c.withZookeeper(zookeeper)
    c.withCollection(collectionName)
    c.withConfiguration(configurationName, configuration)
    c.withSchema(schema)

    c
  }

  def solrPort: Int = container.getSolrPort

  def zookeeperPort: Int = container.getZookeeperPort
}

object SolrContainer {

  val defaultImage = "solr"
  val defaultTag = "8.3.0"
  val defaultDockerImageName = s"$defaultImage:$defaultTag"
  val defaultConfig = new SolrContainerConfiguration()

  case class Def(
    dockerImageName: String = SolrContainer.defaultDockerImageName,
    zookeeper: Boolean = SolrContainer.defaultConfig.isZookeeper,
    collectionName: String = SolrContainer.defaultConfig.getCollectionName,
    configurationName: String = SolrContainer.defaultConfig.getConfigurationName,
    configuration: URL = SolrContainer.defaultConfig.getSolrConfiguration,
    schema: URL = SolrContainer.defaultConfig.getSolrSchema
  ) extends ContainerDef {

    override type Container = SolrContainer

    override def createContainer(): SolrContainer = new SolrContainer(
      dockerImageName,
      zookeeper,
      collectionName,
      configurationName,
      configuration,
      schema
    )
  }
}
