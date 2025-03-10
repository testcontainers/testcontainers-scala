package com.dimafeng.testcontainers

import com.dimafeng.testcontainers.scalatest.TestContainersForAll
import org.scalatest.flatspec.AnyFlatSpec
import org.testcontainers.containers.Network
import org.testcontainers.utility.DockerImageName

class K3sSpec extends AnyFlatSpec with TestContainersForAll {
  override type Containers = K3sContainer

  val network: Network = Network.SHARED

  override def startContainers(): K3sContainer = {
    K3sContainer.Def()
      .withNetwork(network)
      .withNetworkAlias("k3s")
      .start()
  }

  "K3sContainer" should "be started" in withContainers { k3s =>
    val kubeConfig = k3s.generateInternalKubeConfigYaml("k3s")
    k3s.container.getKubeConfigYaml()

    assert(kubeConfig.contains("apiVersion"))
    assert(kubeConfig.contains("kind: \"Config\""))
    assert(kubeConfig.contains("clusters"))
    assert(kubeConfig.contains("users"))
    assert(kubeConfig.contains("contexts"))
    assert(k3s.logs.contains("kube-system"))
    assert(k3s.kubeConfigYaml.contains("apiVersion"))
  }
}
