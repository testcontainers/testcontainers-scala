package com.dimafeng.testcontainers.integration;

import org.testcontainers.images.builder.ImageFromDockerfile;

// workaround for https://github.com/lampepfl/dotty/issues/12586
public class JavaStub {

  public ImageFromDockerfile imageFromDockerFileWithFileFromClasspath(String path, String resourcePath) {
    return new ImageFromDockerfile().withFileFromClasspath(path, resourcePath);
  }

}