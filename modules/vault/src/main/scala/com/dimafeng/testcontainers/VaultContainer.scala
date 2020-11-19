package com.dimafeng.testcontainers;

import org.testcontainers.utility.DockerImageName
import org.testcontainers.vault.{VaultContainer => JavaVaultContainer}

class VaultContainer(dockerImageNameOverride: Option[DockerImageName] = None,
                     vaultToken: Option[String] = None,
                     @deprecated vaultPort: Option[Int] = None,
                     secrets: Option[VaultContainer.Secrets] = None) extends SingleContainer[JavaVaultContainer[_]] {

  val vaultContainer: JavaVaultContainer[_] = {
    if (dockerImageNameOverride.isEmpty) {
      new JavaVaultContainer()
    } else {
      new JavaVaultContainer(dockerImageNameOverride.get)
    }
  }

  if (vaultToken.isDefined) vaultContainer.withVaultToken(vaultToken.get)
  if (vaultPort.isDefined) vaultContainer.withVaultPort(vaultPort.get)
  secrets.foreach { x =>
    vaultContainer.withSecretInVault(x.path, x.firstSecret, x.secrets: _*)
  }

  override val container: JavaVaultContainer[_] = vaultContainer
}

object VaultContainer {

  val defaultDockerImageName = "vault:1.1.3"

  case class Secrets(
    path: String,
    firstSecret: String,
    secrets: Seq[String]
  )

  def apply(dockerImageNameOverride: DockerImageName = null,
            vaultToken: String = null,
            @deprecated vaultPort: Option[Int] = None,
            secrets: Option[VaultContainer.Secrets] = None): VaultContainer = new VaultContainer(
    Option(dockerImageNameOverride),
    Option(vaultToken),
    vaultPort,
    secrets
  )

  case class Def(
    dockerImageName: DockerImageName = DockerImageName.parse(defaultDockerImageName),
    vaultToken: Option[String] = None,
    @deprecated vaultPort: Option[Int] = None,
    secrets: Option[VaultContainer.Secrets] = None
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
