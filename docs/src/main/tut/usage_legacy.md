# Scalacheck LEGACY API

This describes the LEGACY API syntax (pre 0.34)

## Container types

### Generic Container

The most flexible but less convenient container type is `GenericContainer`. This container allows to launch any docker image with custom configuration.

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