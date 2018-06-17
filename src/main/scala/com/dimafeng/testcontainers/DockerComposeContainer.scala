package com.dimafeng.testcontainers

import java.io.File
import java.util
import java.util.function.Consumer

import com.dimafeng.testcontainers.DockerComposeContainer.ComposeFile
import org.testcontainers.containers.output.OutputFrame
import org.testcontainers.containers.wait.strategy.{Wait, WaitStrategy}
import org.testcontainers.containers.{DockerComposeContainer => OTCDockerComposeContainer}
import org.testcontainers.utility.Base58

import scala.collection.JavaConverters._

object ExposedService {
  def apply(name: String, port: Int, instance: Int, waitStrategy: WaitStrategy): ExposedService =
    ExposedService(name, port, waitStrategy, Option(instance))

  def apply(name: String, port: Int, instance: Int): ExposedService =
    ExposedService(name, port, Wait.defaultWaitStrategy(), Option(instance))
}

final case class ExposedService(name: String, port: Int, waitStrategy: WaitStrategy = Wait.defaultWaitStrategy(), instance: Option[Int] = None)

final case class ScaledService(name: String, numInstances: Int)

final case class ServiceLogConsumer(serviceName: String, consumer: Consumer[OutputFrame])

object DockerComposeContainer {
  val ID_LENGTH = 6

  case class ComposeFile(source: Either[File, Seq[File]])

  implicit def toExposedService(oldExposedServices: Map[String, Int]): Seq[ExposedService] =
    oldExposedServices.map { case (name, port) => ExposedService(name, port) }.toSeq

  implicit def fileToEither(file: File): ComposeFile = ComposeFile(Left(file))

  implicit def filesToEither(files: Seq[File]): ComposeFile = ComposeFile(Right(files))

  def randomIdentifier: String = Base58.randomString(DockerComposeContainer.ID_LENGTH).toLowerCase()

  @deprecated("Please use expanded `apply` method")
  def apply(composeFiles: ComposeFile,
            exposedService: Map[String, Int],
            identifier: String): DockerComposeContainer =
    apply(composeFiles, exposedService, identifier)


  @deprecated("Please use expanded `apply` method")
  def apply(composeFiles: ComposeFile,
            exposedService: Map[String, Int]): DockerComposeContainer =
    new DockerComposeContainer(composeFiles, exposedService)

  def apply(composeFiles: ComposeFile,
            exposedServices: Seq[ExposedService] = Seq.empty,
            identifier: String = DockerComposeContainer.randomIdentifier,
            scaledServices: Seq[ScaledService] = Seq.empty,
            pull: Boolean = true,
            localCompose: Boolean = true,
            env: Map[String, String] = Map.empty,
            tailChildContainers: Boolean = false,
            logConsumers: Seq[ServiceLogConsumer] = Seq.empty): DockerComposeContainer =
    new DockerComposeContainer(composeFiles, exposedServices, identifier, scaledServices, pull, localCompose, env, tailChildContainers, logConsumers)
}

class DockerComposeContainer (composeFiles: ComposeFile,
                              exposedServices: Seq[ExposedService] = Seq.empty,
                              identifier: String = DockerComposeContainer.randomIdentifier,
                              scaledServices: Seq[ScaledService] = Seq.empty,
                              pull: Boolean = true,
                              localCompose: Boolean = true,
                              env: Map[String, String] = Map.empty,
                              tailChildContainers: Boolean = false,
                              logConsumers: Seq[ServiceLogConsumer] = Seq.empty)
  extends TestContainerProxy[OTCDockerComposeContainer[_]] {

  type OTCContainer = OTCDockerComposeContainer[T] forSome {type T <: OTCDockerComposeContainer[T]}

  override val container: OTCContainer = {
    val container: OTCContainer = new OTCDockerComposeContainer(identifier, composeFiles match {
      case ComposeFile(Left(f)) => util.Arrays.asList(f)
      case ComposeFile(Right(files)) => files.asJava
    })

    exposedServices.foreach { service =>
      if (service.instance.isDefined) {
        container.withExposedService(service.name, service.instance.get, service.port, service.waitStrategy)
      } else {
        container.withExposedService(service.name, service.port, service.waitStrategy)
      }
    }

    scaledServices.foreach { service =>
      container.withScaledService(service.name, service.numInstances)
    }

    container.withPull(pull)
    container.withLocalCompose(localCompose)
    container.withEnv(env.asJava)
    container.withTailChildContainers(tailChildContainers)

    logConsumers.foreach { serviceLogConsumer =>
      container.withLogConsumer(serviceLogConsumer.serviceName, serviceLogConsumer.consumer)
    }

    container
  }

  def getServiceHost(serviceName: String, servicePort: Int): String = container.getServiceHost(serviceName, servicePort)

  def getServicePort(serviceName: String, servicePort: Int): Int = container.getServicePort(serviceName, servicePort)

}