package com.dimafeng.testcontainers.implicits

import org.testcontainers.utility.DockerImageName

trait DockerImageNameConverters {
  implicit def stringToDockerImageName(s: String): DockerImageName =
    DockerImageName.parse(s)
}

object DockerImageNameConverters extends DockerImageNameConverters