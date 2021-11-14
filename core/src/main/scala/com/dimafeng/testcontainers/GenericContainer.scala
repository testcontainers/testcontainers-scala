package com.dimafeng.testcontainers

import java.util.concurrent.Future

import com.dimafeng.testcontainers.GenericContainer.DockerImage
import org.testcontainers.containers.wait.strategy.WaitStrategy
import org.testcontainers.containers.{BindMode, GenericContainer => JavaGenericContainer}
import org.testcontainers.images.ImagePullPolicy

import scala.collection.JavaConverters._

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
    waitStrategy: Option[WaitStrategy] = None,
    labels: Map[String, String] = Map.empty,
    tmpFsMapping: Map[String, String] = Map.empty,
    imagePullPolicy: Option[ImagePullPolicy] = None
  ) = this({
    val underlying: JavaGenericContainer[_] = dockerImage match {
      case DockerImage(Left(imageFromDockerfile)) => new JavaGenericContainer(imageFromDockerfile)
      case DockerImage(Right(imageName))          => new JavaGenericContainer(imageName)
    }

    if (exposedPorts.nonEmpty) {
      underlying.withExposedPorts(exposedPorts.map(int2Integer): _*)
    }
    env.foreach{ case (k, v) => underlying.withEnv(k, v) }
    if (command.nonEmpty) {
      underlying.withCommand(command: _*)
    }
    classpathResourceMapping.foreach{ case (r, c, m) => underlying.withClasspathResourceMapping(r, c, m) }
    waitStrategy.foreach(underlying.waitingFor)

    if (labels.nonEmpty) {
      underlying.withLabels(labels.asJava)
    }

    if (tmpFsMapping.nonEmpty) {
      underlying.withTmpFs(tmpFsMapping.asJava)
    }

    imagePullPolicy.foreach(underlying.withImagePullPolicy)

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
            waitStrategy: WaitStrategy = null,
            labels: Map[String, String] = Map.empty,
            tmpFsMapping: Map[String, String] = Map.empty,
            imagePullPolicy: ImagePullPolicy = null): GenericContainer =
    new GenericContainer(dockerImage, exposedPorts, env, command, classpathResourceMapping, Option(waitStrategy), labels, tmpFsMapping,
    Option(imagePullPolicy))

  abstract class Def[C <: GenericContainer](init: => C) extends ContainerDef {
    override type Container = C
    protected def createContainer(): C = init
  }

  object Def {

    private final case class Default(dockerImage: DockerImage,
                                     exposedPorts: Seq[Int] = Seq(),
                                     env: Map[String, String] = Map(),
                                     command: Seq[String] = Seq(),
                                     classpathResourceMapping: Seq[(String, String, BindMode)] = Seq(),
                                     waitStrategy: WaitStrategy = null,
                                     labels: Map[String, String] = Map.empty,
                                     tmpFsMapping: Map[String, String] = Map.empty,
                                     imagePullPolicy: ImagePullPolicy = null) extends Def[GenericContainer](
      GenericContainer(
        dockerImage, exposedPorts, env, command, classpathResourceMapping, waitStrategy, 
        labels, tmpFsMapping, imagePullPolicy)
    )

    def apply(dockerImage: DockerImage,
              exposedPorts: Seq[Int] = Seq(),
              env: Map[String, String] = Map(),
              command: Seq[String] = Seq(),
              classpathResourceMapping: Seq[(String, String, BindMode)] = Seq(),
              waitStrategy: WaitStrategy = null,
              labels: Map[String, String] = Map.empty,
              tmpFsMapping: Map[String, String] = Map.empty,
              imagePullPolicy: ImagePullPolicy = null): GenericContainer.Def[GenericContainer] = 
      Default(
        dockerImage, exposedPorts, env, command, classpathResourceMapping, waitStrategy, 
        labels, tmpFsMapping, imagePullPolicy)

  }

}
