package com.dimafeng.testcontainers;

import org.testcontainers.containers.{GenericContainer => OTCGenericContainer}
import org.testcontainers.vault.{VaultContainer => OTCVaultContainer}

class VaultContainer(dockerImageNameOverride: Option[String] = None,
                     vaultToken: Option[String] = None,
                     vaultPort: Option[Int]) extends SingleContainer[OTCVaultContainer[_]] {

  type OTCContainer = OTCGenericContainer[T] forSome {type T <: OTCVaultContainer[T]}

  val vaultContainer: OTCVaultContainer[Nothing] = {
    if (dockerImageNameOverride.isEmpty) {
      new OTCVaultContainer()
    } else {
      new OTCVaultContainer(dockerImageNameOverride.get)
    }
  }

  if (vaultToken.isDefined) vaultContainer.withVaultToken(vaultToken.get)
  if (vaultPort.isDefined) vaultContainer.withVaultPort(vaultPort.get)

  override val container: OTCVaultContainer[_] = vaultContainer
}

object VaultContainer {

  def apply(dockerImageNameOverride: String = null,
            vaultToken: String = null,
            vaultPort: Option[Int] = None): VaultContainer = new VaultContainer(
    Option(dockerImageNameOverride),
    Option(vaultToken),
    vaultPort
  )

}
