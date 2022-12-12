
# Testcontainers-scala release notes

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


