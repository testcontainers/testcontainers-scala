![Logo](logo.png)

[![Build Status](https://travis-ci.org/testcontainers/testcontainers-scala.svg?branch=master)](https://travis-ci.org/testcontainers/testcontainers-scala)
[![Maven Central](https://img.shields.io/maven-central/v/com.dimafeng/testcontainers-scala_2.12.svg)](https://maven-badges.herokuapp.com/maven-central/com.dimafeng/testcontainers-scala_2.12)

# Testcontainers-scala

Scala wrapper for [testcontainers-java](https://github.com/testcontainers/testcontainers-java) that
allows using docker containers for functional/integration/~~unit~~ testing.

> TestContainers is a Java 8 library that supports JUnit tests, providing lightweight, throwaway instances of common databases, Selenium web browsers, or anything else that can run in a Docker container.

Testcontainers-scala in action: http://dimafeng.com/2016/08/01/testcontainers-selenium/

## Slack

How does one get an invite to the Slack channel? I can't seem to join it on my own. Thanks!

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
  override val container = DockerComposeContainer(new File("src/test/resources/docker-compose.yml"), exposedServices = Seq(ExposedService("redis_1", 6379)))

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
"org.testcontainers" % "selenium" % "1.8.0"
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

override val container = MultipleContainers(container1, container2)
```

### Fixed Host Port Containers

This container will allow you to map container ports to statically defined ports on the docker host.

```scala
...
val container = FixedHostPortGenericContainer("nginx:latest",
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

## New API

Starting from 0.34.0 version testcontainers-scala provides the new API. 
The main motivation points are in the [pull request](https://github.com/testcontainers/testcontainers-scala/pull/78).

**This API is experimental and may change!**

### `Container` and `ContainerDef` 

Docker containers are represented through the two different entities:
1. `ContainerDef` — it's container definition. `ContainerDef` describes, how to build a container.
   You can think about it like about a container constructor, or dockerfile description.
   Usually, `ContainerDef` receives some parameters.
   `ContainerDef` has a `start()` method. It returns a started `Container`.
2. `Container` — it's a started container. You can interact with it through its methods.
   For example, in the case of `MySQLContainer` you can get it's JDBC URL with `jdbcUrl` method.
   `Container` is the main entity for using inside tests.

### Scalatest usage

You can use one of the four traits:
1. `TestContainerForAll` — will start a single container before all tests and stop after all tests.
2. `TestContainerForEach` — will start a single container before each test and stop after each test.
3. `TestContainersForAll` — will start multiple containers before all tests and stop after all tests.
4. `TestContainersForEach` — will start multiple containers before each test and stop after each test.


#### Single container in tests

If you want to use a single container in your test:
```scala
class MysqlSpec extends FlatSpec with TestContainerForAll {

  // You need to override `containerDef` with needed container definition
  override val containerDef = MySQLContainer.Def()

  // To use containers in tests you need to use `withContainers` function
  it should "test" in withContainers { mysqlContainer =>
    // Inside your test body you can do with your container whatever you want to
    assert(mysqlContainer.jdbcUrl.nonEmpty)
  }
}
```

Usage of `TestContainerForEach` is not different from the example above.

#### Multiple containers in tests

If you want to use multiple containers in your test:
```scala
class ExampleSpec extends FlatSpec with TestContainersForAll {

  // First of all, you need to declare, which containers you want to use
  override type Containers = MySQLContainer and PostgreSQLContainer

  // After that, you need to describe, how you want to start them,
  // In this method you can use any intermediate logic.
  // You can pass parameters between containers, for example.
  override def startContainers(): Containers = {
    val container1 = MySQLContainer.Def().start()
    val container2 = PostgreSQLContainer.Def().start()
    container1 and container2
  }
  
  // `withContainers` function supports multiple containers:
  it should "test" in withContainers { case mysqlContainer and pgContainer =>
    // Inside your test body you can do with your containers whatever you want to
    assert(mysqlContainer.jdbcUrl.nonEmpty && pgContainer.jdbcUrl.nonEmpty)
  }
  
}
```

Usage of `TestContainersForEach` is not different from the example above.

### `GenericContainer` usage

To create a custom container, which is not built-in in the library, you need to use `GenericContainer`.

For example, you want to create a custom nginx container:
```scala
class NginxContainer(port: Int, underlying: GenericContainer) extends GenericContainer(underlying) {
  // you can add any methods or fields inside your container's body
  def rootUrl: String = s"http://$containerIpAddress:${mappedPort(port)}/"
}
object NginxContainer {

  // In the container definition you need to describe, how your container will be constructed:
  case class Def(port: Int) extends GenericContainer.Def[NginxContainer](
    new NginxContainer(port, GenericContainer(
      dockerImage = "nginx:latest",
      exposedPorts = Seq(port),
      waitStrategy = Wait.forHttp("/")
    ))
  )
}
```

### Migration from the classic API

1. If you have custom containers created with the `GenericContainer`, add `ContainerDef` in the companion like this:
   ```scala
   object MyCustomContainer {
     case class Def(/*constructor params here*/) extends GenericContainer.Def[MyCustomContainer](
       new MyCustomContainer(/*constructor params here*/)
     )
   }
   ```
2. If you are using `ForEachTestContainer`:
    1. If your test contains only one container, replace `ForEachTestContainer` with `TestContainerForEach`
    2. If your test contains multiple containers, replace `ForEachTestContainer` with `TestContainersForEach`
3. If you are using `ForAllTestContainer`:
    1. If your test contains only one container, replace `ForAllTestContainer` with `TestContainerForAll`
    2. If your test contains multiple containers, replace `ForAllTestContainer` with `TestContainersForAll`
4. Fix all compilation errors using compiler messages and examples above.

If you have any questions or difficulties feel free to ask it in our [slack channel](https://testcontainers.slack.com/messages/CAFK4GL85).

## Release notes

* **0.34.0**
    * Added new, experimental API and DSL.
      The main motivation points are in the [pull request](https://github.com/testcontainers/testcontainers-scala/pull/78). 
      Old API remains the same, so all your old code will continue to work.      
      We will wait for the user's feedback about the new API. 
      If it will be positive, eventually this API may replace the current API.
      You can find more information about the new API above.

* **0.33.0**
    * TODO

* **0.32.0**
    * TestContainers -> `1.12.1`
    * SBT -> `1.3.0`

* **0.31.0**
    * Additional config options for PostgreSQL [#70](https://github.com/testcontainers/testcontainers-scala/pull/70)

* **0.30.0**
    * TestContainers -> `1.12.0`
    * Scala -> `2.12.9`

* **0.29.0**
    * TestContainers `1.11.2` -> `1.11.4`

* **0.28.0**
    * `VaultContainer`

* **0.27.0**
    * New `TestLifecycleAware` trait introduced. You can use it when you want to do something with the container before or after the test.
    * `Container` now implements `Startable` interface with `start` and `stop` methods.
    * Old container's lifecycle methods `finished`, `succeeded`, `starting`, `failed` are deprecated. Use `start`, `stop`, and `TestLifecycleAware` methods instead.
    * Added `KafkaContainer`
    * Added `CassandraContainer`
    
* **0.26.0**
    * TestContainers `1.11.2` -> `1.11.3`
    * Scala 2.13.0

* **0.25.0**
    * TestContainers `1.11.1` -> `1.11.2`

* **0.24.0**
    * TestContainers `1.10.6` -> `1.11.1`
    * Scala 2.13.0-M5

* **0.23.0**
    * TestContainers `1.10.1` -> `1.10.6`

* **0.22.0**
    * TestContainers `1.9.1` -> `1.10.1`

* **0.21.0**
    * TestContainers `1.8.3` -> `1.9.1`

* **0.20.0**
    * TestContainers `1.8.0` -> `1.8.3`

* **0.19.0**
    * TestContainers `1.7.3` -> `1.8.0`
    * (#24) `DockerComposeContainer` enhancements
    * Added Dockerfile support to `GenericContainer`

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
