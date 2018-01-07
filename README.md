![Logo](logo.png)

[![Build Status](https://travis-ci.org/testcontainers/testcontainers-scala.svg?branch=master)](https://travis-ci.org/testcontainers/testcontainers-scala)

# Testcontainers-scala


Scala wrapper for [testcontainers-java](https://github.com/testcontainers/testcontainers-java) that
allows using docker containers for functional/integration/~~unit~~ testing.

> TestContainers is a Java 8 library that supports JUnit tests, providing lightweight, throwaway instances of common databases, Selenium web browsers, or anything else that can run in a Docker container.

Testcontainers-scala in action: http://dimafeng.com/2016/08/01/testcontainers-selenium/

## Why can't I use testcontainers-java in my scala project?

*testcontainers-java* is awesome and yes, you can use it in scala project but:

* It's written to be used in JUnit tests
* `DockerComposeContainer<SELF extends DockerComposeContainer<SELF>>` - it's not convinient to use its api with
this 'recursive generic' from scala

Plus

* This wrapper provides with scala interfaces, approaches, types
* This wrapper is integrated with [scalatest](http://www.scalatest.org/)

## Setup

*Maven*

```xml
<!-- Scala 2.11.* -->
<dependency>
    <groupId>com.dimafeng</groupId>
    <artifactId>testcontainers-scala_2.11</artifactId>
    <version>0.13.0</version>
    <scope>test</scope>
</dependency>

<!-- Scala 2.12.* -->
<dependency>
    <groupId>com.dimafeng</groupId>
    <artifactId>testcontainers-scala_2.12</artifactId>
    <version>0.13.0</version>
    <scope>test</scope>
</dependency>
```

*Gradle*

```groovy
testCompile("com.dimafeng:testcontainers-scala_2.11:0.13.0") // Scala 2.11.*
testCompile("com.dimafeng:testcontainers-scala_2.12:0.13.0") // Scala 2.12.*
```

*SBT*

```scala
libraryDependencies += "com.dimafeng" %% "testcontainers-scala" % "0.13.0" % "test"
```

## Requirements

* JDK >= 1.8
* [See 'Compatibility' section](https://www.testcontainers.org/compatibility.html)

## Quick Start

There are two modes of container launching: `ForEachTestContainer` and `ForAllTestContainer`.
The first one starts a new container before each test case and then stops and removes it. The second one
 starts and stops a container only once.

To start using it, you just need to extend one of those traits and override a `container` val as follows:

```scala
import com.dimafeng.testcontainers.{ForAllTestContainer, MySQLContainer}

class MysqlSpec extends FlatSpec with ForAllTestContainer {

  override val container = MySQLContainer()

  it should "do something" in {
    Class.forName(container.driverClassName)
    val connection = DriverManager.getConnection(container.jdbcUrl, container.username, container.password)
    ...
  }
}
```

This spec has a clean mysql database instance for each of its test cases.

```scala
import org.testcontainers.containers.MySQLContainer

class MysqlSpec extends FlatSpec with ForAllTestContainer {

    override val container = MySQLContainer()

    it should "do something" in {
      ...
    }

    it should "do something 2" in {
      ...
    }
}
```

This spec starts one container and both tests share the container's state.


## Container types

### Generic Container

The most flexible but less convinient containtainer type is `GenericContainer`. This container allows to launch any docker image
with custom configuration.

```scala
class GenericContainerSpec extends FlatSpec with ForAllTestContainer {
  override val container = GenericContainer("nginx:latest",
    exposedPorts = Seq(80),
    waitStrategy = Wait.forHttp("/")
  )

  "GenericContainer" should "start nginx and expose 80 port" in {
    assert(Source.fromInputStream(
      new URL(s"http://${container.containerIpAddress}:${container.mappedPort(80)}/").openConnection().getInputStream
    ).mkString.contains("If you see this page, the nginx web server is successfully installed"))
  }
}
```

### Docker Compose

```scala
class ComposeSpec extends FlatSpec with ForAllTestContainer {
  override val container = DockerComposeContainer(new File("src/test/resources/docker-compose.yml"), exposedService = Map("redis_1" -> 6379))

  "DockerComposeContainer" should "retrieve non-0 port for any of services" in {
    assert(container.getServicePort("redis_1", 6379) > 0)
  }
}
```

### Selenium

Before you can use this type of containers, you need to add the following dependencies to your project:

```
"org.seleniumhq.selenium" % "selenium-java" % "2.53.1"
```
and
```
"org.testcontainers" % "selenium" % "1.1.8"
```

Now you can write a test in this way:

```
class SeleniumSpec extends FlatSpec with SeleniumTestContainerSuite with WebBrowser {
  override def desiredCapabilities = DesiredCapabilities.chrome()

  "Browser" should "show google" in {
      go to "http://google.com"
  }
}

```

In this case, you'll obtain a clean instance of browser (firefox/chrome) within container to which
a test will connect via remote-driver. See [Webdriver Containers](https://www.testcontainers.org/usage/webdriver_containers.html)
for more details.

### Mysql

Requires you to add [this dependency](http://mvnrepository.com/artifact/org.testcontainers/mysql)

```scala
class MysqlSpec extends FlatSpec with ForAllTestContainer {

  override val container = MySQLContainer()

  "Mysql container" should "be started" in {
    Class.forName(container.driverClassName)
    val connection = DriverManager.getConnection(container.jdbcUrl, container.username, container.password)
      ...
  }
}
```

### PostgreSQL

Requires you to add [this dependency](https://mvnrepository.com/artifact/org.testcontainers/postgresql)

```scala
class PostgresqlSpec extends FlatSpec with ForAllTestContainer  {

  override val container = PostgreSQLContainer()

  "PostgreSQL container" should "be started" in {
    Class.forName(container.driverClassName)
    val connection = DriverManager.getConnection(container.jdbcUrl, container.username, container.password)
      ...
  }
}
```

### Multiple Containers

```scala
...
val container = MultipleContainers(MySQLContainer(), GenericContainer(...))

// access to containers
containers.containers._1.containerId // container id of the first container
...

```

### Start/Stop hooks

If you want to execute your code after container start or before container stop you can override `afterStart()` and `beforeStop()` methods.

```scala
class MysqlSpec extends FlatSpec with ForAllTestContainer {

  ...

  override def beforeStop(): Unit = {
    // your code
  }

  override def afterStart(): Unit = {
    // your code
  }
}
```

## Release notes

* **0.13.0**
    * TestContainers `1.4.3` -> `1.5.1`
    * Scala 2.10 support

* **0.12.0**
    * Improvement: `afterStart` hook now handles exceptions correctly

* **0.11.0**
    * Improvement: containers don't start in `ForAllTestContainer` if all tests are ignored

* **0.10.0**
    * TestContainers `1.4.2` -> `1.4.3`
    * Fix of #8

* **0.8.0**
    * PostgreSQL container

* **0.7.0**
    * TestContainers `1.2.1` -> `1.4.2`

* **0.6.0**
    * TestContainers `1.2.0` -> `1.2.1`
    * Fix of the `afterStart` hook 

* **0.5.0**
    * TestContainers `1.1.8` -> `1.2.0`

* **0.4.1**
    * TestContainers `1.1.7` -> `1.1.8`

* **0.4.0**
    * TestContainers `1.1.5` -> `1.1.7`
    * Scala cross-building (2.11.* + 2.12.*)
    
* **0.3.0**
    * TestContainers `1.1.0` -> `1.1.5`
    * Start/Stop hooks

* **0.2.0**
    * TestContainers `1.0.5` -> `1.1.0`
    * Code refactoring
    * Scala wrappers for major container types

## Publishing

1. Check that `./publish.sh` contains all scala versions for publishing
2. Run script `./publish.sh`

## License

The MIT License (MIT)

Copyright (c) 2016 Dmitry Fedosov

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit
persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
