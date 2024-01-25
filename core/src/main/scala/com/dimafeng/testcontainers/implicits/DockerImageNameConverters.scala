package com.dimafeng.testcontainers.implicits

import org.testcontainers.utility.DockerImageName

trait DockerImageNameConverters {
  @deprecated("Use `DockerImageName` in Container constructor instead", "v0.38.7")
  implicit def stringToDockerImageName(s: String): DockerImageName =
    DockerImageName.parse(s)
}

object DockerImageNameConverters extends DockerImageNameConverters