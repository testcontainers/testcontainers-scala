![Logo](logo.png)

[![Build Status](https://travis-ci.org/testcontainers/testcontainers-scala.svg?branch=master)](https://travis-ci.org/testcontainers/testcontainers-scala)
[![Maven Central](https://img.shields.io/maven-central/v/com.dimafeng/testcontainers-scala_2.12.svg)](https://maven-badges.herokuapp.com/maven-central/com.dimafeng/testcontainers-scala_2.12)

# Testcontainers-scala

Scala wrapper for [testcontainers-java](https://github.com/testcontainers/testcontainers-java) that
allows using docker containers for functional/integration/~~unit~~ testing.

> TestContainers is a Java 8 library that supports JUnit tests, providing lightweight, throwaway instances of common databases, Selenium web browsers, or anything else that can run in a Docker container.

Testcontainers-scala in action: http://dimafeng.com/2016/08/01/testcontainers-selenium/

## Why can't I use testcontainers-java in my scala project?

*testcontainers-java* is awesome and yes, you can use it in scala projects as is **but**:

* It's integrated with JUnit only
* There are some problems initializing containers due to `DockerComposeContainer<SELF extends DockerComposeContainer<SELF>>`

As a side bonus, you will have:

* Scala friendly interfaces, approaches, types
* Integration with [ScalaTest](http://www.scalatest.org/)

## Slack

[Slack channel](https://testcontainers.slack.com/messages/CAFK4GL85)

## Setup

*SBT*

```scala
libraryDependencies += "com.dimafeng" %% "testcontainers-scala" % testcontainersScalaVersion % "test"
```

## Requirements

* JDK >= 1.8
* [See 'Compatibility' section](https://www.testcontainers.org/compatibility.html)

## Quick Start

There are two ScalaTest aware traits:
* `ForEachTestContainer` starts a new container(s) before each test case and then stops and removes it.
* `ForAllTestContainer` starts and stops a container only once for all test cases within the spec.


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

The most flexible but less convenient container type is `GenericContainer`. This container allows to launch any docker image
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

The container can also be customized using the constructor parameters, this snippet will initialize a docker container from a specific docker image, with a specific schema name and specific username/password
```scala
class MysqlSpec extends FlatSpec with ForAllTestContainer {

  override val container = MySQLContainer(mysqlImageVersion = "mysql:5.7.18",
                                          databaseName = "testcontainer-scala",
                                          username = "scala",
                                          password = "scala")

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

If you need to test more than one container in your test, you could use `MultipleContainers` for that. Just define your containers and pass them to the `MultipleContainers` constructor:
```scala
val mySqlContainer = MySQLContainer()
val genericContainer = GenericContainer(...)

override val container = MultipleContainers(mySqlContainer, genericContainer)
```

#### Dependent containers

If configuration of one container depends on runtime state of another one, you should define your containers as `lazy`:

```scala
lazy val container1 = Container1()
lazy val container2 = Container2(container1.port)

override val containers = MultipleContainers(pgContainer, appContainer)
```

### Fixed Host Port Containers

This container will allow you to map container ports to statically defined ports on the docker host.

```scala
...
val container = FixedHostPortGenericGenericContainer("nginx:latest",
    waitStrategy = Wait.forHttp("/"),
    exposedHostPort = 8090,
    exposedContainerPort = 80
  )
```

### Custom configuration of inner containers

All container types have constructor methods with most popular parameters. In case you're missing some custom option from `testcontainers-java`, there is
a method that provides an elegant way to tune the nested container. **It's not recommended to access inner container directly.**

```
override val container = MySQLContainer().configure { c =>
    c.withNetwork(...)
    c.withStartupAttempts(...)
  }
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

* **0.18.0**
    * TestContainers `1.7.1` -> `1.7.3`

* **0.17.0**
    * Testcontainers `1.6.0` -> `1.7.1`
    * Removed `shapeless` dependency
    * Added implicit conversion to `LazyContainer`. This gives you a possibility to not wrap your containers into the `LazyContainer` manually.
    * `MultipleContainers.apply` now receives `LazyContainer[_]*` type. Together with the previous point, it makes usage experience of `MultipleContainers` more smooth.
    * Added multiple missing reflecting methods to all containers
    * Added `configure` method. See [this](#custom-configuration-of-inner-containers) for more details

* **0.16.0**
    * `FixedHostPortGenericContainer` added

* **0.15.0**
    * Additional configuration parameters for `MySQLContainer`
    * Improvements to `MultipleContainers` - container lazy creation for dependent containers

* **0.14.0**
    * TestContainers `1.5.1` -> `1.6.0`

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

`$ sbt clean release`
