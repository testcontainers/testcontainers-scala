# Testcontainers-scala usage

Since this project is essentially a scala wrapper, please familiarize yourself with the [Testcontainer documentation](https://www.testcontainers.org/) first.

This section describes the syntax valid since version 0.34.0. See this [pull request](https://github.com/testcontainers/testcontainers-scala/pull/78) for the motivation of the change. **The legacy API description is [available here](usage_legacy.md)**.

**This API is experimental and may change!**

## Test traits

Depending on the expected behaviour, you can use one of the following four traits to mix in into your test:

1. `TestContainerForAll` — will start a single container before all tests and stop after all tests.
2. `TestContainerForEach` — will start a single container before each test and stop after each test.
3. `TestContainersForAll` — will start multiple containers before all tests and stop after all tests.
4. `TestContainersForEach` — will start multiple containers before each test and stop after each test.

In the scalatest version of the library, those traits are located in the `com.dimafeng.testcontainers.scalatest` package, whereas for the MUnit version they are in `com.dimafeng.testcontainers.munit`.


## `Container` and `ContainerDef` 

Docker containers are represented through the two different entities:
1. `ContainerDef` — it's container definition. `ContainerDef` describes, how to build a container.
   You can think about it like about a container constructor, or dockerfile description.
   Usually, `ContainerDef` receives some parameters.
   `ContainerDef` has a `start()` method. It returns a started `Container`.
2. `Container` — it's a started container. You can interact with it through its methods.
   For example, in the case of `MySQLContainer` you can get it's JDBC URL with `jdbcUrl` method.
   `Container` is the main entity for using inside tests.


## Single generic container

The most flexible but less convenient container type is `GenericContainer`. This container allows to launch any docker image with custom configuration.

### ScalaCheck

```scala
import com.dimafeng.testcontainers.GenericContainer
import com.dimafeng.testcontainers.scalatest.TestContainerForAll
import org.scalatest.flatspec._
import org.testcontainers.containers.wait.strategy.Wait
import java.net.URL
import scala.io.Source

class GenericContainerSpec extends AnyFlatSpec with TestContainerForAll {

  override val containerDef = GenericContainer.Def(
    "nginx:latest",
    exposedPorts = Seq(80),
    waitStrategy = Wait.forHttp("/")
  )

  "GenericContainer" should "start nginx and expose 80 port" in {
    withContainers { mysqlContainer =>
      assert(
        Source
          .fromInputStream(
            new URL(
              s"http://${mysqlContainer.containerIpAddress}:${mysqlContainer.mappedPort(80)}/"
            ).openConnection().getInputStream
          )
          .mkString
          .contains(
            "If you see this page, the nginx web server is successfully installed"
          )
      )
    }
  }
}
```

### MUnit

The Munit version of this test is almost identical (note the slight difference in imports):

```scala
import com.dimafeng.testcontainers.GenericContainer
import com.dimafeng.testcontainers.munit.TestContainerForAll
import munit.FunSuite
import org.testcontainers.containers.wait.strategy.Wait
import java.net.URL
import scala.io.Source

class GenericContainerSpec extends FunSuite with TestContainerForAll {

  override val containerDef = GenericContainer.Def(
    "nginx:latest",
    exposedPorts = Seq(80),
    waitStrategy = Wait.forHttp("/")
  )

  test("GenericContainer should start nginx and expose 80 port ") {
    withContainers { mysqlContainer =>
      assert(
        Source
          .fromInputStream(
            new URL(
              s"http://${mysqlContainer.containerIpAddress}:${mysqlContainer.mappedPort(80)}/"
            ).openConnection().getInputStream
          )
          .mkString
          .contains(
            "If you see this page, the nginx web server is successfully installed"
          )
      )
    }
  }
}
```

From now on, the code examples in this document will be based on Scalatest only, although it should be clear by now how to adapt the imports for MUnit.

## Docker compose

Assuming a docker compose configuration file is available in `src/it/resources/docker-compose.yml`: 

```scala
import com.dimafeng.testcontainers.DockerComposeContainer
import com.dimafeng.testcontainers.ExposedService
import com.dimafeng.testcontainers.scalatest.TestContainerForAll
import org.scalatest.flatspec._
import org.testcontainers.containers.wait.strategy.Wait 

class ComposeContainerSpec extends AnyFlatSpec with TestContainerForAll {

  override val containerDef =
    DockerComposeContainer.Def(
      new File("src/it/resources/docker-compose.yml"),
      tailChildContainers = true,
      exposedServices = Seq(
        ExposedService("postgres_1", 5432, Wait.forLogMessage(".*database system is ready to accept connections.*", 2))
      )
    )

  "DockerComposeContainer" should "retrieve non-0 port for any of services" in {
    withContainers { composedContainers =>
      assert(composedContainers.getServicePort("postgres_1", 5432) > 0)
    }
  }

}
```

## Backend-specific modules

Backend-specific modules offer a convenient syntax that can be manipulated directly from the test. A few of them are illustrated below, and a long list of what's available is present in [Setup](setup.md).


### Mysql

Required supplementaries libraries: 

```scala
"com.dimafeng" %% "testcontainers-scala-mysql" % testcontainersScalaVersion % "it",
"mysql" % "mysql-connector-java" % "8.0.31"
```

Integration test:

```scala
import com.dimafeng.testcontainers.MySQLContainer
import com.dimafeng.testcontainers.scalatest.TestContainerForAll
import org.scalatest.flatspec.AnyFlatSpec
import org.testcontainers.utility.DockerImageName
import java.sql.DriverManager

class MysqlSpec extends AnyFlatSpec with TestContainerForAll {

  override val containerDef = MySQLContainer.Def(
    dockerImageName = DockerImageName.parse("mysql:8.0.31"),
    databaseName = "testcontainer-scala",
    username = "scala",
    password = "scala"
  )

  "Mysql container" should "be started" in {
    withContainers { mysqlContainer =>
      Class.forName(mysqlContainer.driverClassName)
      val connection =
        DriverManager.getConnection(mysqlContainer.jdbcUrl, mysqlContainer.username, mysqlContainer.password)
      assert(!connection.isClosed())
    }
  }
}
```

### PostgreSQL

Required supplementaries libraries: 

```scala
libraryDependencies ++= Seq(
   "com.dimafeng" %% "testcontainers-scala-postgresql" % testcontainersScalaVersion % "it",
   "org.postgresql" % "postgresql" % "42.5.1"
)
```

Integration test:

```scala
import com.dimafeng.testcontainers.PostgreSQLContainer
import com.dimafeng.testcontainers.scalatest.TestContainerForAll
import org.scalatest.flatspec.AnyFlatSpec
import org.testcontainers.utility.DockerImageName
import java.sql.DriverManager

class PgSpec extends AnyFlatSpec with TestContainerForAll {

  override val containerDef = PostgreSQLContainer.Def(
    dockerImageName = DockerImageName.parse("postgres:15.1"),
    databaseName = "testcontainer-scala",
    username = "scala",
    password = "scala"
  )

  "PostgreSQL container" should "be started" in {
    withContainers { pgContainer =>
      Class.forName(pgContainer.driverClassName)
      val connection = DriverManager.getConnection(pgContainer.jdbcUrl, pgContainer.username, pgContainer.password)
      assert(!connection.isClosed())
    }
  }
}
```

## Multiple Containers

If you want to use multiple containers for all tests in your suite:

```scala
import com.dimafeng.testcontainers.DockerComposeContainer
import com.dimafeng.testcontainers.MySQLContainer
import com.dimafeng.testcontainers.PostgreSQLContainer
import com.dimafeng.testcontainers.lifecycle.and
import com.dimafeng.testcontainers.scalatest.TestContainersForAll
import org.scalatest.flatspec.AnyFlatSpec
import java.io.File

// First of all, you need to declare, which containers you want to use
class ExampleSpec extends AnyFlatSpec with TestContainersForAll {

  // First of all, you need to declare, which containers you want to use
  override type Containers = MySQLContainer and PostgreSQLContainer and DockerComposeContainer

  // After that, you need to describe, how you want to start them,
  // In this method you can use any intermediate logic.
  // You can pass parameters between containers, for example.
  override def startContainers(): Containers = {
    val container1 = MySQLContainer.Def().start()
    val container2 = PostgreSQLContainer.Def().start()
    val container3 =
      DockerComposeContainer
        .Def(DockerComposeContainer.ComposeFile(Left(new File("src/it/resources/docker-compose.yml"))))
        .start()
    container1 and container2 and container3
  }

  // `withContainers` function supports multiple containers:
  it should "test" in withContainers { case mysqlContainer and pgContainer and dcContainer =>
    // Inside your test body you can do with your containers whatever you want to
    assert(mysqlContainer.jdbcUrl.nonEmpty && pgContainer.jdbcUrl.nonEmpty)
  }

}
```


## Start/Stop hooks
You have the option to override `afterContainersStart` and `beforeContainersStop` methods.

### Example with single container

```scala
import com.dimafeng.testcontainers.PostgreSQLContainer
import com.dimafeng.testcontainers.scalatest.TestContainersForAll
import org.scalatest.flatspec.AnyFlatSpec
import org.testcontainers.utility.DockerImageName

class MySpec extends AnyFlatSpec with TestContainerForAll {

  override val containerDef =
    PostgreSQLContainer.Def(DockerImageName.parse("postgres:15.1"))

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

  it should "work" in withContainers { case pgContainer: PostgreSQLContainer =>
    assert(pgContainer.jdbcUrl.nonEmpty)
  }
}
```

### Example with multiple containers

```scala
import com.dimafeng.testcontainers.MySQLContainer
import com.dimafeng.testcontainers.PostgreSQLContainer
import com.dimafeng.testcontainers.lifecycle.and
import com.dimafeng.testcontainers.scalatest.TestContainersForAll
import org.scalatest.flatspec.AnyFlatSpec

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

## Specific notes on MUnit usage

### Using fixtures

If you're using MUnit,  you can use the fixtures under `TestContainersFixture` instead of the `*ForAll`/`*ForEach` traits:

```scala
import com.dimafeng.testcontainers.munit.fixtures.TestContainersFixtures
import com.dimafeng.testcontainers.MySQLContainer
import munit.FunSuite

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
import com.dimafeng.testcontainers.munit.fixtures.TestContainersFixtures
import com.dimafeng.testcontainers.MySQLContainer
import munit.FunSuite

class MysqlSpecFun extends FunSuite with TestContainersFixtures {

  val mysql = ContainerFunFixture(MySQLContainer())

  mysql.test("test case name") { container =>
    // Inside your test body you can do with your container whatever you want to
    assert(container.jdbcUrl.nonEmpty)
  }
}
```

### Lifecycle 

- If you use `*ForAll` trait and override beforeAll() without calling super.beforeAll() your containers won't start.
- If you use `*ForAll` trait and override afterAll() without calling super.afterAll() your containers won't stop.
- If you use `*ForEach` trait and override beforeEach() without calling super.beforeEach() your containers won't start.
- If you use `*ForEach` trait and override afterEach() without calling super.afterEach() your containers won't stop.
- [Currently,](https://github.com/scalameta/munit/issues/119) there is no way to retrieve test status in MUnit `afterEach` block, so `afterTest` hook will never contain an error. 

If you have any questions or difficulties feel free to ask it in our [slack channel](https://testcontainers.slack.com/messages/CAFK4GL85).
