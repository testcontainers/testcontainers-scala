package com.dimafeng.testcontainers.implicits

import org.testcontainers.utility.DockerImageName

trait DockerImageNameConverters {
  @deprecated("Use `DockerImageName` in Container constructor instead")
  implicit def stringToDockerImageName(s: String): DockerImageName =
    DockerImageName.parse(s)
}

object DockerImageNameConverters extends DockerImageNameConverters