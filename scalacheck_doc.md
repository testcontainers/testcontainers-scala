![Logo](logo.png)

[![Build Status](https://github.com/testcontainers/testcontainers-scala/actions/workflows/test.yml/badge.svg)](https://github.com/testcontainers/testcontainers-scala/actions/workflows/test.yml)
[![Maven Central](https://img.shields.io/maven-central/v/com.dimafeng/testcontainers-scala_2.12.svg)](https://maven-badges.herokuapp.com/maven-central/com.dimafeng/testcontainers-scala_2.12)

# Testcontainers-scala

Scala wrapper for [testcontainers-java](https://github.com/testcontainers/testcontainers-java) that
allows using docker containers for functional/integration/~~unit~~ testing.

> TestContainers is a Java 8 library that supports JUnit tests, providing lightweight, throwaway instances of common databases, Selenium web browsers, or anything else that can run in a Docker container.

[testcontainers-scala in action](http://dimafeng.com/2016/08/01/testcontainers-selenium)

## Slack

[Slack channel](https://testcontainers.slack.com/messages/CAFK4GL85)

[Invite link for slack](https://slack.testcontainers.org/)

## Requirements

* JDK >= 1.8
* [See 'Compatibility' section](https://www.testcontainers.org/compatibility.html)


## Setup

Although Testcontainer tests are mere unit tests, it's best to use them as [sbt integration tests](https://www.scala-sbt.org/1.x/docs/Testing.html#Integration+Tests) since they spawn a separate environment, can be slower and require specific configuration. 

```scala
lazy val root = (project in file("."))
  .configs(IntegrationTest)
  .settings(
    Defaults.itSettings,
    ...
)    
```

Sbt integration tests are stored in `src/it/scala/` and executed by running the following sbt command:

```scala
> IntegrationTest/test
```

Add testcontainer-scala to your sbt dependencies. A different library is used for scalatest than for MUnit:

* For scalatest users:
```scala
libraryDependencies += "com.dimafeng" %% "testcontainers-scala-scalatest" % testcontainersScalaVersion % "it"
```

* For MUnit users:
```scala
libraryDependencies += "com.dimafeng" %% "testcontainers-scala-munit" % testcontainersScalaVersion % "it"
```

The library above will enable you to launch a generic docker container or docker-compose environment from your tests. Optionally, you can also add supplementary libraries for technology-specific containers (e.g. Postgres, Kafka, NGinx,...), see the [Module](#modules) section below.


Next, enable [sbt forking](https://www.scala-sbt.org/1.x/docs/Forking.html#Forking) to run the tests in a separate JVM from sbt. This allows for graceful shutdown of containers once the tests have finished running.

```scala
IntegrationTest / fork := true,
```

## Modules

Testcontainers-scala is modular. All modules has the same version. To depend on some module put this into your `build.sbt` file: 
```scala
libraryDependencies += "com.dimafeng" %% moduleName % testcontainersScalaVersion % "it"
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
* `testcontainers-scala-gcloud` — module with the Bigtable, Firebase and PubSub emulator containers.

Most of the modules are just proxies to the testcontainers-java modules and behave exactly like java containers.
You can find documentation about them in the [testcontainers-java docs pages](https://www.testcontainers.org/).

## API

This section describes the syntax valid since version 0.34.0. See this [pull request](https://github.com/testcontainers/testcontainers-scala/pull/78) for the motivation of the change .

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

### Test traits

You can use one of the four traits:
1. `TestContainerForAll` — will start a single container before all tests and stop after all tests.
2. `TestContainerForEach` — will start a single container before each test and stop after each test.
3. `TestContainersForAll` — will start multiple containers before all tests and stop after all tests.
4. `TestContainersForEach` — will start multiple containers before each test and stop after each test.

### Writting tests



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

Additionally, you have available MUnit fixtures integrations under `TestContainersFixtures`:

1. `ForAllContainerFixture` — will start a single container before all tests and stop after all tests.
2. `ForEachContainerFixture` — will start a single container before each test and stop after each test.
3. `ContainerFunFixture` — will start a single container before each test and stop after each test.

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

#### Using fixtures

Instead of the `*ForAll`/`*ForEach` traits you can use the fixtures under `TestContainersFixture`:

```scala
class MysqlSpec extends FunSuite with TestContainersFixtures {

  // Use `ForAllContainerFixture` to start/stop container before/after all tests
  val mysql = ForEachContainerFixture(MySQLContainer())

  // You need to override `munitFixtures` and pass in your container fixture
  override def munitFixtures = List(mysql)

  test("test case name") {
    // Inside your test body you can do with your container whatever you want to
    assert(mysql().jdbcUrl.nonEmpty)
  }
}
```

There is also available a `FunFixture` version for containers:

```scala
class MysqlSpec extends FunSuite with TestContainersFixtures {

  val mysql = ContainerFunFixture(MySQLContainer())

  mysql.test("test case name") { container =>
    // Inside your test body you can do with your container whatever you want to
    assert(container.jdbcUrl.nonEmpty)
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

* **0.40.8**
    * Fixed `MockServer` container tag
    * Added `ContainerDef` constructors for `gcloud` containers

* **0.40.6**
    * Added `PubSubEmulatorContainer`

* **0.40.5**
    * Kafka docker image for ARM
    * Updated `localstack` dependencies

* **0.40.4**
    * `startupCheckStrategy` for `FixedHostPortGenericContainer` and `GenericContainer` https://github.com/testcontainers/testcontainers-scala/pull/215

* **0.40.3**
    * MUnit fixtures https://github.com/testcontainers/testcontainers-scala/pull/214

* **0.40.2**
    * Added `BigtableEmulatorContainer`
    * Refactoring of `KafkaContainer`: https://github.com/testcontainers/testcontainers-scala/pull/209

* **0.40.1**
    * Added `FirestoreEmulatorContainer`

* **0.40.0**
    * Added file system bindings https://github.com/testcontainers/testcontainers-scala/pull/206
    * **Breaking change:** `classpathResourceMapping` is now `Seq[FileSystemBind]`

* **0.39.11**
    * Generic container instantiation - https://github.com/testcontainers/testcontainers-scala/pull/194

* **0.39.10**
    * testcontainers-java updated to 1.16.2

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
