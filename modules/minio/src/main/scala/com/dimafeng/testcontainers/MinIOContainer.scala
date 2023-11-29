package com.dimafeng.testcontainers

import org.testcontainers.containers.{MinIOContainer => JavaMinIOContainer}
import org.testcontainers.utility.DockerImageName

case class MinIOContainer(
                           dockerImageName: DockerImageName = DockerImageName.parse(MinIOContainer.defaultDockerImageName),
                           userName: String = MinIOContainer.defaultUserName,
                           password: String = MinIOContainer.defaultPassword
                         ) extends SingleContainer[JavaMinIOContainer] {

  override val container: JavaMinIOContainer = {
    val c = new JavaMinIOContainer(dockerImageName)
    c.withUserName(userName)
    c.withPassword(password)
    c
  }

  def s3URL: String = container.getS3URL()
}

object MinIOContainer {

  val defaultImage = "minio/minio"
  val defaultTag = "RELEASE.2023-09-04T19-57-37Z"
  val defaultDockerImageName = s"$defaultImage:$defaultTag"

  val defaultUserName = "miniouser"
  val defaultPassword = "miniopassword"

  case class Def(dockerImageName: DockerImageName = DockerImageName.parse(MinIOContainer.defaultDockerImageName),
    userName: String = MinIOContainer.defaultUserName, password: String = MinIOContainer.defaultPassword) extends ContainerDef {
    override type Container = MinIOContainer

    override def createContainer(): MinIOContainer = {
      new MinIOContainer(dockerImageName, userName, password)
    }
  }
}