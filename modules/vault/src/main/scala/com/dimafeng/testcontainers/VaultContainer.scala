package com.dimafeng.testcontainers;

import org.testcontainers.vault.{VaultContainer => JavaVaultContainer}

class VaultContainer(dockerImageNameOverride: Option[String] = None,
                     vaultToken: Option[String] = None,
                     vaultPort: Option[Int]) extends SingleContainer[JavaVaultContainer[_]] {

  val vaultContainer: JavaVaultContainer[Nothing] = {
    if (dockerImageNameOverride.isEmpty) {
      new JavaVaultContainer()
    } else {
      new JavaVaultContainer(dockerImageNameOverride.get)
    }
  }

  if (vaultToken.isDefined) vaultContainer.withVaultToken(vaultToken.get)
  if (vaultPort.isDefined) vaultContainer.withVaultPort(vaultPort.get)

  override val container: JavaVaultContainer[_] = vaultContainer
}

object VaultContainer {

  val defaultDockerImageName = "vault:0.7.0"

  def apply(dockerImageNameOverride: String = null,
            vaultToken: String = null,
            vaultPort: Option[Int] = None): VaultContainer = new VaultContainer(
    Option(dockerImageNameOverride),
    Option(vaultToken),
    vaultPort
  )

  case class Def(
    dockerImageName: String = defaultDockerImageName,
    vaultToken: Option[String] = None,
    vaultPort: Option[Int] = None
  ) extends ContainerDef {

    override type Container = VaultContainer

    override def createContainer(): VaultContainer = {
      new VaultContainer(
        dockerImageNameOverride = Some(dockerImageName),
        vaultToken = vaultToken,
        vaultPort = vaultPort
      )
    }
  }

}
