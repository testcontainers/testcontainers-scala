package com.dimafeng.testcontainers

import java.io.File
import java.util.function.Consumer

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

  type OTCContainer = OTCDockerComposeContainer[T] forSome {type T <: OTCDockerComposeContainer[T]}

  implicit def toExposedService(oldExposedServices: Map[String, Int]): Seq[ExposedService] =
    oldExposedServices.map { case (name, port) => ExposedService(name, port) }.toSeq

  def apply(file: File, exposedService: Map[String, Int]): DockerComposeContainer =
    DockerComposeContainer(Seq(file), exposedService)

  protected[testcontainers] def randomIdentifier = Base58.randomString(DockerComposeContainer.ID_LENGTH).toLowerCase()
}

final case class DockerComposeContainer(composeFiles: Seq[File],
                                        exposedServices: Seq[ExposedService] = Seq.empty,
                                        identifier: String = DockerComposeContainer.randomIdentifier,
                                        scaledServices: Seq[ScaledService] = Seq.empty,
                                        pull: Boolean = true,
                                        localCompose: Boolean = true,
                                        env: Map[String, String] = Map.empty,
                                        tailChildContainers: Boolean = false,
                                        logConsumers: Seq[ServiceLogConsumer] = Seq.empty) extends TestContainerProxy[OTCDockerComposeContainer[_]] {

  override val container: DockerComposeContainer.OTCContainer = {
    val container: DockerComposeContainer.OTCContainer = new OTCDockerComposeContainer(identifier, composeFiles.asJava)

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