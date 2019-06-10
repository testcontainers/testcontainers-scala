package com.dimafeng.testcontainers

import java.util.concurrent.Future

import com.dimafeng.testcontainers.GenericContainer.DockerImage
import org.testcontainers.containers.wait.strategy.WaitStrategy
import org.testcontainers.containers.{BindMode, GenericContainer => OTCGenericContainer}

class GenericContainer(dockerImage: DockerImage,
                       exposedPorts: Seq[Int] = Seq(),
                       env: Map[String, String] = Map(),
                       command: Seq[String] = Seq(),
                       classpathResourceMapping: Seq[(String, String, BindMode)] = Seq(),
                       waitStrategy: Option[WaitStrategy] = None
                      ) extends SingleContainer[OTCGenericContainer[_]] {

  type OTCContainer = OTCGenericContainer[T] forSome {type T <: OTCGenericContainer[T]}
  override implicit val container: OTCContainer = dockerImage match {
    case DockerImage(Left(imageFromDockerfile)) => new OTCGenericContainer(imageFromDockerfile)
    case DockerImage(Right(imageName))          => new OTCGenericContainer(imageName)
  }

  if (exposedPorts.nonEmpty) {
    container.withExposedPorts(exposedPorts.map(int2Integer): _*)
  }
  env.foreach(Function.tupled(container.withEnv))
  if (command.nonEmpty) {
    container.withCommand(command: _*)
  }
  classpathResourceMapping.foreach(Function.tupled(container.withClasspathResourceMapping))
  waitStrategy.foreach(container.waitingFor)
}

object GenericContainer {
  case class DockerImage(image: Either[String, Future[String]])

  implicit def javaFutureToDockerImage(javaFuture: Future[String]): DockerImage = {
    DockerImage(Right(javaFuture))
  }

  implicit def stringToDockerImage(imageName: String): DockerImage = {
    DockerImage(Left(imageName))
  }

  def apply(dockerImage: DockerImage,
            exposedPorts: Seq[Int] = Seq(),
            env: Map[String, String] = Map(),
            command: Seq[String] = Seq(),
            classpathResourceMapping: Seq[(String, String, BindMode)] = Seq(),
            waitStrategy: WaitStrategy = null): GenericContainer =
    new GenericContainer(dockerImage, exposedPorts, env, command, classpathResourceMapping, Option(waitStrategy))
}
