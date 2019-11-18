package com.dimafeng.testcontainers

import java.util.concurrent.Future

import com.dimafeng.testcontainers.GenericContainer.DockerImage
import org.testcontainers.containers.wait.strategy.WaitStrategy
import org.testcontainers.containers.{BindMode, GenericContainer => JavaGenericContainer}

class GenericContainer(
  override val underlyingUnsafeContainer: JavaGenericContainer[_]
) extends SingleContainer[JavaGenericContainer[_]] {

  override implicit val container: JavaGenericContainer[_] = underlyingUnsafeContainer

  def this(
    dockerImage: DockerImage,
    exposedPorts: Seq[Int] = Seq(),
    env: Map[String, String] = Map(),
    command: Seq[String] = Seq(),
    classpathResourceMapping: Seq[(String, String, BindMode)] = Seq(),
    waitStrategy: Option[WaitStrategy] = None
  ) = this({
    val underlying: JavaGenericContainer[_] = dockerImage match {
      case DockerImage(Left(imageFromDockerfile)) => new JavaGenericContainer(imageFromDockerfile)
      case DockerImage(Right(imageName))          => new JavaGenericContainer(imageName)
    }

    if (exposedPorts.nonEmpty) {
      underlying.withExposedPorts(exposedPorts.map(int2Integer): _*)
    }
    env.foreach(Function.tupled(underlying.withEnv))
    if (command.nonEmpty) {
      underlying.withCommand(command: _*)
    }
    classpathResourceMapping.foreach(Function.tupled(underlying.withClasspathResourceMapping))
    waitStrategy.foreach(underlying.waitingFor)

    underlying
  })

  def this(genericContainer: GenericContainer) = this(genericContainer.underlyingUnsafeContainer)
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

  abstract class Def[C <: GenericContainer](init: => C) extends ContainerDef {
    override type Container = C
    protected def createContainer(): C = init
  }
}
