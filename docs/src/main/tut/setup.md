# Testcontainers-scala setup

## Requirements

* JDK >= 1.8
* [See 'Compatibility' section](https://www.testcontainers.org/compatibility.html)


## Sbt setup

Although Testcontainer tests are mere unit tests, it's best (although not mandatory) to use them as [sbt integration tests](https://www.scala-sbt.org/1.x/docs/Testing.html#Integration+Tests) since they spawn a separate environment, can be slower and require specific configuration. 

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

Add testcontainer-scala to your sbt dependencies. A different library is used for Scalatest than for MUnit:

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
IntegrationTest / fork := true
```

## Modules

Testcontainers-scala is modular. All modules has the same version. To depend on some module put this into your `build.sbt` file: 

```scala
libraryDependencies += "com.dimafeng" %% moduleName % testcontainersScalaVersion % "it"
```

Here is the full list of the [currently available modules](https://github.com/testcontainers/testcontainers-scala/tree/master/modules):

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

