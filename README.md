![Logo](logo.png)

[![Build Status](https://travis-ci.org/testcontainers/testcontainers-scala.svg?branch=master)](https://travis-ci.org/testcontainers/testcontainers-scala)
[![Maven Central](https://img.shields.io/maven-central/v/com.dimafeng/testcontainers-scala_2.12.svg)](https://maven-badges.herokuapp.com/maven-central/com.dimafeng/testcontainers-scala_2.12)

# Testcontainers-scala

Scala wrapper for [testcontainers-java](https://github.com/testcontainers/testcontainers-java) that
allows using docker containers for functional/integration/~~unit~~ testing.

> TestContainers is a Java 8 library that supports JUnit tests, providing lightweight, throwaway instances of common databases, Selenium web browsers, or anything else that can run in a Docker container.

Testcontainers-scala in action: http://dimafeng.com/2016/08/01/testcontainers-selenium/

## Slack

[Slack channel](https://testcontainers.slack.com/messages/CAFK4GL85)

[Invite link for slack](https://slack.testcontainers.org/)

## Setup

For scalatest users:
```scala
libraryDependencies += "com.dimafeng" %% "testcontainers-scala-scalatest" % testcontainersScalaVersion % "test"
```

For MUnit users:
```scala
libraryDependencies += "com.dimafeng" %% "testcontainers-scala-munit" % testcontainersScalaVersion % "test"
```

## Requirements

* JDK >= 1.8
* [See 'Compatibility' section](https://www.testcontainers.org/compatibility.html)

## Quick Start

First of all, let's add scalatest and MySQL testcontainers modules in the `build.sbt` file to play with:

```scala
libraryDependencies ++= Seq(
  "com.dimafeng" %% "testcontainers-scala-scalatest" % testcontainersScalaVersion % "test",
  "com.dimafeng" %% "testcontainers-scala-mysql" % testcontainersScalaVersion % "test",
)
```

Next, add the following line to run tests in a separate JVM from sbt. This allows for graceful shutdown of containers once the tests have finished running.

```scala
Test / fork := true,
```

There are two ScalaTest aware traits:
* `ForEachTestContainer` starts a new container(s) before each test case and then stops and removes it.
* `ForAllTestContainer` starts and stops a container only once for all test cases within the spec.


To start using it, you just need to extend one of those traits and override a `container` val as follows:

```scala
import com.dimafeng.testcontainers.{ForAllTestContainer, MySQLContainer}

class MysqlSpec extends AnyFlatSpec with ForAllTestContainer {

  override val container: MySQLContainer = MySQLContainer()

  it should "do something" in {
    Class.forName(container.driverClassName)
    val connection = DriverManager.getConnection(container.jdbcUrl, container.username, container.password)
    ...
  }
}
```

Be sure to override `container` with a `val` not a `def`, otherwise you will start a new container each 
time you call `container` and this is likely to fail your tests.

This spec has a clean mysql database instance for each of its test cases.

```scala
import org.testcontainers.containers.MySQLContainer

class MysqlSpec extends AnyFlatSpec with ForAllTestContainer {

    override val container: MySQLContainer = MySQLContainer()

    it should "do something" in {
      ...
    }

    it should "do something 2" in {
      ...
    }
}
```

This spec starts one container and both tests share the container's state.

Most of available container classes allow you to provide custom image name or version
instead of default one set in the library.

In order to provide custom image name you need to pass `DockerImageName` object.

```scala
override val container: MongoDBContainer = MongoDBContainer(DockerImageName.parse("mongo:4.0.10"))
```

Starting from testcontainers-java 1.15.0 container classes execute image compatibility checks during initialization
(for more details, see this [pull request](https://github.com/testcontainers/testcontainers-java/pull/3021)).
If you want to use custom image that is compatible with selected container class implementation,
it must be explicitly marked as compatible with default image.

```scala
override val container: MongoDBContainer = MongoDBContainer(DockerImageName.parse("myregistry/mongo:4.0.10").asCompatibleSubstituteFor("mongo"))
```

Providing custom image name as `String` is currently deprecated.
Implicit conversion method is available in ScalaTest and MUnit traits.

```scala
class MongoSpec extends AnyFlatSpec with ForAllTestContainer {
    //deprecated implicit conversion
    override val container: MongoDBContainer = MongoDBContainer("mongo:4.0.10")
}
```

## Modules

Testcontainers-scala is modular. All modules has the same version. To depend on some module put this into your `build.sbt` file: 
```scala
libraryDependencies += "com.dimafeng" %% moduleName % testcontainersScalaVersion % "test"
```

Here is the full list of the currently available modules:

* `testcontainers-scala-core` — core module. 
  It contains some basic building blocks of the library and no integration with any test frameworks. 
  You probably will not use it directly, because all other modules depend on it.
* `testcontainers-scala-scalatest` — Scalatest integration module.
* `testcontainers-scala-munit` — MUnit integration module.
* `testcontainers-scala-scalatest-selenium` — module to use the Selenium container with the Scalatest.
* `testcontainers-scala-mysql` — module with the MySQL container.
* `testcontainers-scala-postgresql` — module with the PostgreSQL container.
* `testcontainers-scala-cassandra` — module with the Cassandra container.
* `testcontainers-scala-kafka` — module with the Kafka container.
* `testcontainers-scala-vault` — module with the Vault container.
* `testcontainers-scala-oracle-xe` — module with the Oracle container.
* `testcontainers-scala-neo4j` — module with the Neo4J server container.
* `testcontainers-scala-mssqlserver` — module with the MsSQL server container.
* `testcontainers-scala-clickhouse` — module with the ClickHouse container.
* `testcontainers-scala-cockroachdb` — module with the CockroachDB container.
* `testcontainers-scala-couchbase` — module with the Couchbase container.
* `testcontainers-scala-db2` — module with the DB2 container.
* `testcontainers-scala-dynalite` — module with the Dynalite container.
* `testcontainers-scala-elasticsearch` — module with the Elastic search container.
* `testcontainers-scala-influxdb` — module with the InfluxDB container.
* `testcontainers-scala-localstack` — module with the Localstack container.
* `testcontainers-scala-localstack-v2` — module with the Localstack V2 container.
* `testcontainers-scala-mariadb` — module with the MariaDB container.
* `testcontainers-scala-mockserver` — module with the MockServer container.
* `testcontainers-scala-nginx` — module with the Nginx container.
* `testcontainers-scala-pulsar` — module with the Pulsar container.
* `testcontainers-scala-rabbitmq` — module with the RabbitMQ container.
* `testcontainers-scala-toxiproxy` — module with the ToxiProxy container.
* `testcontainers-scala-orientdb` — module with the OrientDB container.
* `testcontainers-scala-presto` — module with the Presto container.
* `testcontainers-scala-trino` — module with the Trino container.
* `testcontainers-scala-mongodb` — module with the MongoDB container.
* `testcontainers-scala-solr` — module with the Solr container.

Most of the modules are just proxies to the testcontainers-java modules and behave exactly like java containers.
You can find documentation about them in the [testcontainers-java docs pages](https://www.testcontainers.org/).

## Container types

### Generic Container

The most flexible but less convenient container type is `GenericContainer`. This container allows to launch any docker image
with custom configuration.

```scala
class GenericContainerSpec extends AnyFlatSpec with ForAllTestContainer {
  override val container: GenericContainer = GenericContainer("nginx:latest",
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
class ComposeSpec extends AnyFlatSpec with ForAllTestContainer {
  override val container: DockerComposeContainer = DockerComposeContainer(new File("src/test/resources/docker-compose.yml"), exposedServices = Seq(ExposedService("redis_1", 6379)))

  "DockerComposeContainer" should "retrieve non-0 port for any of services" in {
    assert(container.getServicePort("redis_1", 6379) > 0)
  }
}
```

### Selenium

Before you can use this type of containers, you need to add the following dependencies to your project:

```scala
"com.dimafeng" %% "testcontainers-scala-scalatest-selenium" % testcontainersScalaVersion % "test"
```

Now you can write a test in this way:

```
class SeleniumSpec extends AnyFlatSpec with SeleniumTestContainerSuite with WebBrowser {
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

Requires you to add this dependency:

```scala
"com.dimafeng" %% "testcontainers-scala-mysql" % testcontainersScalaVersion % "test"
```

```scala
class MysqlSpec extends AnyFlatSpec with ForAllTestContainer {

  override val container: MySQLContainer = MySQLContainer()

  "Mysql container" should "be started" in {
    Class.forName(container.driverClassName)
    val connection = DriverManager.getConnection(container.jdbcUrl, container.username, container.password)
      ...
  }
}
```

The container can also be customized using the constructor parameters, this snippet will initialize a docker container from a specific docker image, with a specific schema name and specific username/password
```scala
class MysqlSpec extends AnyFlatSpec with ForAllTestContainer {

  override val container: MySQLContainer = MySQLContainer(mysqlImageVersion = DockerImageName.parse("mysql:5.7.18"),
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

Requires you to add this dependency:

```scala
"com.dimafeng" %% "testcontainers-scala-postgresql" % testcontainersScalaVersion % "test"
```

```scala
class PostgresqlSpec extends AnyFlatSpec with ForAllTestContainer  {

  override val container: PostgreSQLContainer = PostgreSQLContainer()

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

override val container: MultipleContainers = MultipleContainers(mySqlContainer, genericContainer)
```

#### Dependent containers

If configuration of one container depends on runtime state of another one, you should define your containers as `lazy`:

```scala
lazy val container1 = Container1()
lazy val container2 = Container2(container1.port)

override val container: MultipleContainers = MultipleContainers(container1, container2)
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
override val container: MySQLContainer = MySQLContainer().configure { c =>
    c.withNetwork(...)
    c.withStartupAttempts(...)
  }
```

### Start/Stop hooks

If you want to execute your code after container start or before container stop you can override `afterStart()` and `beforeStop()` methods.

```scala
class MysqlSpec extends AnyFlatSpec with ForAllTestContainer {

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
class MysqlSpec extends AnyFlatSpec with TestContainerForAll {

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
class ExampleSpec extends AnyFlatSpec with TestContainersForAll {

  // First of all, you need to declare, which containers you want to use
  override type Containers = MySQLContainer and PostgreSQLContainer and DockerComposeContainer

  // After that, you need to describe, how you want to start them,
  // In this method you can use any intermediate logic.
  // You can pass parameters between containers, for example.
  override def startContainers(): Containers = {
    val container1 = MySQLContainer.Def().start()
    val container2 = PostgreSQLContainer.Def().start()
    val container3 = DockerComposeContainer.Def(ComposeFile(Left(new File("docker-compose.yml")))).start()
    container1 and container2 and container3
  }
  
  // `withContainers` function supports multiple containers:
  it should "test" in withContainers { case mysqlContainer and pgContainer and dcContainer =>
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

However, if you don't want to create a custom container, you can use `GenericContainer` directly while overriding `containerDef`:

```scala
override val containerDef = GenericContainer.Def("nginx:latest",
  exposedPorts = Seq(80),
  waitStrategy = Wait.forHttp("/")
)
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

### MUnit usage

Similarly to Scalatest, you can use one of the four traits:
1. `TestContainerForAll` — will start a single container before all tests and stop after all tests.
2. `TestContainerForEach` — will start a single container before each test and stop after each test.
3. `TestContainersForAll` — will start multiple containers before all tests and stop after all tests.
4. `TestContainersForEach` — will start multiple containers before each test and stop after each test.

#### Single container in tests

If you want to use a single container for all tests in your suite:
```scala
class MysqlSpec extends FunSuite with TestContainerForAll {

  // You need to override `containerDef` with needed container definition
  override val containerDef = MySQLContainer.Def()

  // To use containers in tests you need to use `withContainers` function
  test("test case name") {
    withContainers { mysqlContainer =>
      // Inside your test body you can do with your container whatever you want to
      assert(mysqlContainer.jdbcUrl.nonEmpty)
    }
  }
}
```

If you want to use a single container for each test in your suite just use code above with `TestContainerForEach` trait instead of `TestContainerForAll`.

#### Multiple containers in tests

If you want to use multiple containers for all tests in your suite:
```scala
class ExampleSpec extends FunSuite with TestContainersForAll {

  // First of all, you need to declare, which containers you want to use
  override type Containers = MySQLContainer and PostgreSQLContainer and DockerComposeContainer

  // After that, you need to describe, how you want to start them,
  // In this method you can use any intermediate logic.
  // You can pass parameters between containers, for example.
  override def startContainers(): Containers = {
    val container1 = MySQLContainer.Def().start()
    val container2 = PostgreSQLContainer.Def().start()
    val container3 = DockerComposeContainer.Def(ComposeFile(Left(new File("docker-compose.yml")))).start()
    container1 and container2 and container3
  }

  // `withContainers` function supports multiple containers:
  test("test") {
    withContainers { case mysqlContainer and pgContainer and dcContainer =>
      // Inside your test body you can do with your containers whatever you want to
      assert(mysqlContainer.jdbcUrl.nonEmpty && pgContainer.jdbcUrl.nonEmpty)
    }
  }
}
```

If you want to use a single container for each test in your suite just use code above with `TestContainersForEach` trait instead of `TestContainersForAll`.

#### Start/Stop hooks
You have the option to override `afterContainersStart` and `beforeContainersStop` methods.

##### Example with single container
```scala
class MySpec extends AnyFlatSpec with TestContainerForAll {

  override val containerDef: ContainerDef =
    PostgreSQLContainer.Def(DockerImageName.parse("postgres:12"))

  override def afterContainersStart(container: Containers): Unit = {
    super.afterContainersStart(container)

    container match {
      case _: PostgreSQLContainer => println("your logic here")
    }
  }

  override def beforeContainersStop(container: Containers): Unit = {
    super.beforeContainersStop(container)

    container match {
      case _: PostgreSQLContainer => println("your logic here")
    }
  }

 it should "work" in withContainers { 
   case pgContainer: PostgreSQLContainer =>
     assert(pgContainer.jdbcUrl.nonEmpty) 
  }
}
```

##### Example with multiple containers
```scala
class MySpec extends AnyFlatSpec with TestContainersForAll {

  override type Containers = MySQLContainer and PostgreSQLContainer

  override def startContainers(): Containers = {
    val container1 = MySQLContainer.Def().start()
    val container2 = PostgreSQLContainer.Def().start()

    container1 and container2
  }

  override def afterContainersStart(containers: Containers): Unit = {
    super.afterContainersStart(containers)

    containers match {
      case mySqlContainer and pgContainer => println("your logic here")
    }
  }

  override def beforeContainersStop(containers: Containers): Unit = {
    super.beforeContainersStop(containers)

    containers match {
      case mySqlContainer and pgContainer => println("your logic here")
    }
  }

  it should "work" in withContainers {
    case mysqlContainer and pgContainer =>
      assert(mysqlContainer.jdbcUrl.nonEmpty && pgContainer.jdbcUrl.nonEmpty)
  }

}
```

#### Notes on MUnit usage
- If you use `*ForAll` trait and override beforeAll() without calling super.beforeAll() your containers won't start.
- If you use `*ForAll` trait and override afterAll() without calling super.afterAll() your containers won't stop.
- If you use `*ForEach` trait and override beforeEach() without calling super.beforeEach() your containers won't start.
- If you use `*ForEach` trait and override afterEach() without calling super.afterEach() your containers won't stop.
- [Currently,](https://github.com/scalameta/munit/issues/119) there is no way to retrieve test status in MUnit `afterEach` block, so `afterTest` hook will never contain an error. 

If you have any questions or difficulties feel free to ask it in our [slack channel](https://testcontainers.slack.com/messages/CAFK4GL85).

## Release notes

* **0.39.9**
    * Added `SchemaRegistryContainer`

* **0.39.8**
    * Fixed Scaladex

* **0.39.7**
    * Fix for https://github.com/testcontainers/testcontainers-scala/issues/186

* **0.39.6**
    * Scala 3.0.1 support

* **0.39.5**
    * Scala 3.0.0 support
    * Added the ability to specify services for `DockerComposeContainer`

* **0.39.4**
    * Added `TrinoContainer`

* **0.39.3**
    * testcontainers-java updated to 1.15.2
    
* **0.39.2**
    * Scala updated to 3.0.0-RC1

* **0.39.1**
    * Scala updated to 3.0.0-M3

* **0.39.0**
    * Scalatest updated to 3.2.3

* **0.38.8**
    * testcontainers-java updated to 1.15.1.

* **0.38.7**
    * Addressed testcontainers-java image compatibility checks by changing `String` to `DockerImageName` in Container class constructors

* **0.38.6**
    * testcontainers-java updated to 1.15.0:
        * Include fix for "Can not connect to Ryuk" on macOS with Docker for Mac 2.4.0.0 (https://github.com/testcontainers/testcontainers-java/issues/3166)

* **0.38.5**
    * Added `LocalStackV2Container`

* **0.38.4**
    * Fixed `SolrContainer` configuration nullability

* **0.38.3**
    * Fixed `MongoDBContainer` default version

* **0.38.1**
    * Fixed class cast exception in the next containers:
        * InfluxDBContainer
        * MariaDBContainer
        * MSSQLServerContainer
        * MySQLContainer
        * NginxContainer
        * PostgreSQLContainer
        * PrestoContainer
        * VaultContainer

* **0.38.0**
    * testcontainers-java updated to 1.14.3:
        * Added `MongoDBContainer`.
        * Added `SolrContainer`.
        * Added `urlParams` constructor parameter to `CockroachContainer`, `Db2Container`, `MariaDBContainer`, `MSSQLServerContainer`, `MySQLContainer` and `PostgreSQLContainer`.
        * Added `host` method to containers. This method will replace `containerIpAddress` in the future.
        * `CouchbaseContainer` was rewritten. This change is not backward compatible.
        * Added `vhost` parameter to `RabbitMQContainer.Exchange`.
    * Added `commonJdbcParams` constructor parameter to `CockroachContainer`, `Db2Container`, `MariaDBContainer`, `MSSQLServerContainer`, `MySQLContainer`, `PostgreSQLContainer`, `OracleContainer` and `PrestoContainer`. It contains common options for JDBC containers.

* **0.37.0**
    * Added MUnit integration.

* **0.36.1**
    * Added `.waitingFor()` to `DockerComposeContainer`
    
* **0.36.0**
    * testcontainers-java updated to 1.13.0:
        * Added `OrientDBContainer`.
        * Added `PrestoContainer`.
        * Added `DockerComposeContainer.getContainerByServiceName` method.
    * Change module dependencies for container modules. They now depend on the core module instead of scalatest module.
    * Removed `dbPassword` parameter from the `ClickHouseContainer`. Looks like this parameter was added accidentally (java container doesn't support it).

* **0.35.2**
    * testcontainers-java updated to 1.12.5.
    * Added methods to the `SingleContainer`:
        * `execInContainer`
        * `copyFileToContainer`
        * `copyFileFromContainer`

* **0.35.1**
    * MariaDB NPE fix #106

* **0.35.0**

    From this release testcontainers-scala supports all testcontainers-java containers and methods. If you find missing parts — don't hesitate to create an issue!
    * testcontainers-java updated to 1.12.4.
    * Added missing containers from the testcontainers-java.  Here is the full list of new containers:
        * `testcontainers-scala-neo4j`
        * `testcontainers-scala-mssqlserver`
        * `testcontainers-scala-clickhouse`
        * `testcontainers-scala-cockroachdb`
        * `testcontainers-scala-couchbase`
        * `testcontainers-scala-db2`
        * `testcontainers-scala-dynalite`
        * `testcontainers-scala-elasticsearch`
        * `testcontainers-scala-influxdb`
        * `testcontainers-scala-localstack`
        * `testcontainers-scala-mariadb`
        * `testcontainers-scala-mockserver`
        * `testcontainers-scala-nginx`
        * `testcontainers-scala-pulsar`
        * `testcontainers-scala-rabbitmq`
        * `testcontainers-scala-toxiproxy  `
    * Added missing methods to the `SingleContainer`:
        * `envMap`
        * `boundPortNumbers`
        * `copyToFileContainerPathMap`
        * `labels`
        * `shmSize`
        * `testHostIpAddress`
        * `tmpFsMapping`
        * `logs`
        * `livenessCheckPortNumbers`
    * Added missing parameters to the `GenericContainer` constructor:
        * `labels`
        * `tmpFsMapping`
        * `imagePullPolicy`
    * Added missing methods to the `CassandraContainer`:
        * `cluster`
        * `username`
        * `password`


* **0.34.3**
    * Support of the new API in the `DockerComposeContainer`: added `DockerComposeContainer.Def`.

* **0.34.2**
    * New `OracleContainer`. It is in the `testcontainers-scala-oracle-xe` package.

* **0.34.1**
    * New API improvements:
        * Changed signature of `def withContainers(runTest: Containers => Unit): Unit` to 
        `def withContainers[A](runTest: Containers => A): A`
        * Renamed `afterStart` to `afterContainersStart` and added a `containers: Containers` argument to it.
        * Renamed `beforeStop` to `beforeContainersStop` and added a `containers: Containers` argument to it.

* **0.34.0**
    * Added new, experimental API and DSL.
      The main motivation points are in the [pull request](https://github.com/testcontainers/testcontainers-scala/pull/78). 
      Old API remains the same, so all your old code will continue to work.      
      We will wait for the user's feedback about the new API. 
      If it will be positive, eventually this API may replace the current API.
      You can find more information about the new API above.
    * The library is split into multiple modules.
      Every built-in container now has a separate module with all needed transitive dependencies,
      so you will not have to add them manually. More details are above, in the dedicated paragraph.
      Old module `testcontainers-scala` is still provided but will be eventually dropped in future.
      To migrate to the new modules remove `testcontainers-scala` dependency
      and add only needed dependencies from the modules list in the docs.

* **0.33.0**
    * TestContainers `1.12.1` -> `1.12.2`

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
