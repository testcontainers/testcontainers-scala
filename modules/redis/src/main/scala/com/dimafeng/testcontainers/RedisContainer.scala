package com.dimafeng.testcontainers

import com.redis.testcontainers.{RedisContainer => JavaRedisContainer}
import org.testcontainers.utility.DockerImageName

class RedisContainer(dockerImageName: DockerImageName)
    extends SingleContainer[JavaRedisContainer] {
  override val container: JavaRedisContainer = new JavaRedisContainer(
    dockerImageName
  )

  def redisUri: String = container.getRedisURI
}

object RedisContainer {
  val defaultImage: String = "redis"
  val defaultTag: String = "latest"
  val defaultDockerImageName: String = s"$defaultImage:$defaultTag"

  case class Def(
      dockerImageName: DockerImageName =
        DockerImageName.parse(defaultDockerImageName)
  ) extends ContainerDef {
    override type Container = RedisContainer

    override protected def createContainer(): RedisContainer =
      new RedisContainer(dockerImageName)
  }

  def apply(dockerImageNameOverride: DockerImageName = null): RedisContainer =
    new RedisContainer(
      Option(dockerImageNameOverride).getOrElse(
        DockerImageName.parse(defaultDockerImageName)
      )
    )
}
