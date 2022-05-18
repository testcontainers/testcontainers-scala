package com.dimafeng.testcontainers

import org.testcontainers.containers.{KafkaContainer => JavaKafkaContainer}
import org.testcontainers.utility.DockerImageName

import scala.util.Try
import scala.sys.process._

case class KafkaContainer(dockerImageName: DockerImageName = KafkaContainer.defaultDockerImage
                    ) extends SingleContainer[JavaKafkaContainer] {

  override val container: JavaKafkaContainer = new JavaKafkaContainer(dockerImageName)

  def bootstrapServers: String = container.getBootstrapServers
}

object KafkaContainer {

  private lazy val runningOnArm64: Boolean =
    // This shouldn't fail (`null` if it doesn't exist), however just in case...
    // `os.arch` is `x86_64` if we are on an x86 machine _or_ the JVM is running under Rosetta 2 (emulation).
    // `aarch64` if we are on ARM64 _and_ not running under Rosetta 2 (emulation).
    Try(System.getProperty("os.arch")).toOption.exists {
      // We know we definitely are on an ARM64 processor
      case "aarch64" => true
      // We may be on ARM64 if the JVM is being emulated, find if it is running under Rosetta 2
      case _ => runningUnderRosetta2
    }

  // `sysctl -n sysctl.proc_translated` is defined on Apple ARM64 devices.
  // It returns `0` when running natively and `1` when running the current process under Rosetta 2.
  private lazy val runningUnderRosetta2: Boolean =
    // ProcessLogger redirects any error output away from console to a noop receiver (effectively `/dev/null`)
    Try("sysctl -n sysctl.proc_translated".!!(ProcessLogger(_ => ()))).toOption.exists(_.trim == "1")

  private val confluentKafkaImage = "confluentinc/cp-kafka"

  val defaultImage =
    /*
    * The official Confluent docker image does not work with ARM64.
    * See https://github.com/confluentinc/common-docker/issues/117,
    * https://github.com/confluentinc/kafka-images/issues/80
    */
    if (runningOnArm64) "niciqy/cp-kafka-arm64" else confluentKafkaImage

  val defaultTag = "7.0.1"
  val defaultDockerImageName = s"$defaultImage:$defaultTag"

  private val defaultDockerImage =
    DockerImageName.parse(KafkaContainer.defaultDockerImageName).asCompatibleSubstituteFor(confluentKafkaImage)

  case class Def(dockerImageName: DockerImageName = defaultDockerImage) extends ContainerDef {

    override type Container = KafkaContainer

    override def createContainer(): KafkaContainer = {
      new KafkaContainer(dockerImageName)
    }
  }
}
