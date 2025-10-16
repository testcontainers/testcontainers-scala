package com.dimafeng.testcontainers

import java.io.File
import java.util
import java.util.function.Consumer
import com.dimafeng.testcontainers.DockerComposeContainer.ComposeFile
import org.testcontainers.containers.output.OutputFrame
import org.testcontainers.containers.wait.strategy.{Wait, WaitStrategy}
import org.testcontainers.containers.{ContainerState, ComposeContainer => JavaDockerComposeContainer}
import org.testcontainers.utility.{Base58, DockerImageName}

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

final case class WaitingForService(serviceName: String, waitStrategy: WaitStrategy)

sealed trait Services
object Services {
  case object All extends Services
  final case class Specific(services: Seq[Service]) extends Services
}

final case class Service(name: String)

object DockerComposeContainer {
  val ID_LENGTH = 6

  case class ComposeFile(source: Either[File, Seq[File]])

  implicit def toExposedService(oldExposedServices: Map[String, Int]): Seq[ExposedService] =
    oldExposedServices.map { case (name, port) => ExposedService(name, port) }.toSeq

  implicit def fileToEither(file: File): ComposeFile = ComposeFile(Left(file))

  implicit def filesToEither(files: Seq[File]): ComposeFile = ComposeFile(Right(files))

  def randomIdentifier: String = Base58.randomString(DockerComposeContainer.ID_LENGTH).toLowerCase()

  def apply(composeFiles: ComposeFile,
            exposedServices: Seq[ExposedService] = Seq.empty,
            identifier: String = DockerComposeContainer.randomIdentifier,
            scaledServices: Seq[ScaledService] = Seq.empty,
            pull: Boolean = true,
            env: Map[String, String] = Map.empty,
            tailChildContainers: Boolean = false,
            logConsumers: Seq[ServiceLogConsumer] = Seq.empty,
            waitingFor: Option[WaitingForService] = None,
            services: Services = Services.All,
            image: DockerImageName = DockerImageName.parse("docker")): DockerComposeContainer =
    new DockerComposeContainer(composeFiles,
      exposedServices,
      identifier,
      scaledServices,
      pull,
      env,
      tailChildContainers,
      logConsumers,
      waitingFor,
      services,
      image)

  case class Def(
                  composeFiles: ComposeFile,
                  exposedServices: Seq[ExposedService] = Seq.empty,
                  identifier: String = DockerComposeContainer.randomIdentifier,
                  scaledServices: Seq[ScaledService] = Seq.empty,
                  pull: Boolean = true,
                  env: Map[String, String] = Map.empty,
                  tailChildContainers: Boolean = false,
                  logConsumers: Seq[ServiceLogConsumer] = Seq.empty,
                  waitingFor: Option[WaitingForService] = None,
                  services: Services = Services.All,
                  image: DockerImageName = DockerImageName.parse("docker")
                ) extends ContainerDef {

    override type Container = DockerComposeContainer

    override def createContainer(): DockerComposeContainer = {
      DockerComposeContainer(
        composeFiles,
        exposedServices,
        identifier,
        scaledServices,
        pull,
        env,
        tailChildContainers,
        logConsumers,
        waitingFor,
        services,
        image
      )
    }
  }

}

class DockerComposeContainer(composeFiles: ComposeFile,
                             exposedServices: Seq[ExposedService] = Seq.empty,
                             identifier: String = DockerComposeContainer.randomIdentifier,
                             scaledServices: Seq[ScaledService] = Seq.empty,
                             pull: Boolean = true,
                             env: Map[String, String] = Map.empty,
                             tailChildContainers: Boolean = false,
                             logConsumers: Seq[ServiceLogConsumer] = Seq.empty,
                             waitingFor: Option[WaitingForService] = None,
                             services: Services = Services.All,
                             image: DockerImageName = DockerImageName.parse("docker"))
  extends TestContainerProxy[JavaDockerComposeContainer] {

  override val container: JavaDockerComposeContainer = {
    val container: JavaDockerComposeContainer = new JavaDockerComposeContainer(image, identifier, composeFiles match {
      case ComposeFile(Left(f)) => util.Arrays.asList(f)
      case ComposeFile(Right(files)) => files.asJava
    })

    services match {
      case Services.Specific(services) => container.withServices(services.map(_.name) : _*)
      case Services.All =>
    }

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

    waitingFor.map(waitingForService =>
      container.waitingFor(waitingForService.serviceName, waitingForService.waitStrategy)
    )

    container.withPull(pull)
    container.withEnv(env.asJava)
    container.withTailChildContainers(tailChildContainers)

    logConsumers.foreach { serviceLogConsumer =>
      container.withLogConsumer(serviceLogConsumer.serviceName, serviceLogConsumer.consumer)
    }

    container
  }

  def getServiceHost(serviceName: String, servicePort: Int): String = container.getServiceHost(serviceName, servicePort)

  def getServicePort(serviceName: String, servicePort: Int): Int = container.getServicePort(serviceName, servicePort)

  def getContainerByServiceName(serviceName: String): Option[ContainerState] = {
    val res = container.getContainerByServiceName(serviceName)
    if (res.isPresent) Some(res.get()) else None
  }

  override def start(): Unit = container.start()

  override def stop(): Unit = container.stop()
}
