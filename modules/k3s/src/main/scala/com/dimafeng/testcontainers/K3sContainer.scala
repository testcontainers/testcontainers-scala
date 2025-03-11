package com.dimafeng.testcontainers

import org.testcontainers.k3s.{K3sContainer => JavaK3sContainer}
import org.testcontainers.utility.DockerImageName
import org.testcontainers.containers.Network

class K3sContainer private (
  dockerImageName: DockerImageName,
  network: Option[Network],
  networkAlias: Option[String]
) extends SingleContainer[JavaK3sContainer] {
  override val container: JavaK3sContainer = {
    val c = new JavaK3sContainer(dockerImageName)
    network.fold(c)(c.withNetwork(_))
    networkAlias.fold(c)(c.withNetworkAliases(_))
  }

  def generateInternalKubeConfigYaml(networkAlias: String): String = container.generateInternalKubeConfigYaml(networkAlias)
  def kubeConfigYaml: String = container.getKubeConfigYaml()
}

object K3sContainer {
  private[testcontainers] final val defaultImage: String = "rancher/k3s"
  private[testcontainers] final val defaultTag: String = "v1.32.2-k3s1"
  private[testcontainers] final val defaultDockerImageName: String = s"${defaultImage}:${defaultTag}"

  case class Def(
    dockerImageName: DockerImageName = DockerImageName.parse(defaultDockerImageName),
    network: Option[Network] = None,
    networkAlias: Option[String] = None
  ) extends ContainerDef {
    override type Container = K3sContainer
    override def createContainer(): K3sContainer = new K3sContainer(dockerImageName, network, networkAlias)

    def withNetwork(network: Network): Def = copy(network = Some(network))
    def withNetworkAlias(alias: String): Def = copy(networkAlias = Some(alias))
  }
}
