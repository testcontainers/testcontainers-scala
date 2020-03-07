import sbt._

object Dependencies {
  private def COMPILE(modules: sbt.ModuleID*): Seq[sbt.ModuleID] = deps(None, modules: _*)

  private def PROVIDED(modules: sbt.ModuleID*): Seq[sbt.ModuleID] = deps(Some("provided"), modules: _*)

  private def TEST(modules: sbt.ModuleID*): Seq[sbt.ModuleID] = deps(Some("test"), modules: _*)

  private def deps(scope: Option[String], modules: sbt.ModuleID*): Seq[sbt.ModuleID] = {
    scope.map(s => modules.map(_ % s)).getOrElse(modules)
  }

  private val testcontainersVersion = "1.13.0"
  private val seleniumVersion = "2.53.1"
  private val slf4jVersion = "1.7.25"
  private val scalaTestVersion = "3.0.8"
  private val mysqlConnectorVersion = "5.1.42"
  private val neo4jConnectorVersion = "4.0.0"
  private val oracleDriverVersion = "19.3.0.0"
  private val cassandraDriverVersion = "4.0.1"
  private val postgresqlDriverVersion = "9.4.1212"
  private val kafkaDriverVersion = "2.2.0"
  private val mockitoVersion = "2.27.0"
  private val restAssuredVersion = "4.0.0"
  private val awsVersion = "1.11.479"

  val allOld = Def.setting(
    PROVIDED(
      "org.scalatest" %% "scalatest" % scalaTestVersion
    )
  )

  val core = Def.setting(
    COMPILE(
      "org.testcontainers" % "testcontainers" % testcontainersVersion
    ) ++ PROVIDED(
      "org.slf4j" % "slf4j-simple" % slf4jVersion
    ) ++ TEST(
      "junit" % "junit" % "4.12",
      "org.scalatest" %% "scalatest" % scalaTestVersion,
      "org.testcontainers" % "selenium" % testcontainersVersion,
      "org.postgresql" % "postgresql" % postgresqlDriverVersion,
      "org.mockito" % "mockito-core" % mockitoVersion
    )
  )

  val scalatest = Def.setting(
    PROVIDED(
      "org.scalatest" %% "scalatest" % scalaTestVersion
    )
  )

  val scalatestSelenium = Def.setting(
    COMPILE(
      "org.testcontainers" % "selenium" % testcontainersVersion,
      "org.seleniumhq.selenium" % "selenium-java" % seleniumVersion
    )
  )

  val jdbc = Def.setting(
    COMPILE(
      "org.testcontainers" % "jdbc" % testcontainersVersion
    )
  )

  val moduleMysql = Def.setting(
    COMPILE(
      "org.testcontainers" % "mysql" % testcontainersVersion
    ) ++ TEST(
      "mysql" % "mysql-connector-java" % mysqlConnectorVersion
    )
  )

  val moduleNeo4j = Def.setting(
    COMPILE(
      "org.testcontainers" % "neo4j" % testcontainersVersion
    ) ++ TEST(
      "org.neo4j.driver" % "neo4j-java-driver" % neo4jConnectorVersion
    )
  )
  val modulePostgres = Def.setting(
    COMPILE(
      "org.testcontainers" % "postgresql" % testcontainersVersion
    ) ++ TEST(
      "org.postgresql" % "postgresql" % postgresqlDriverVersion
    )
  )

  val moduleOracle = Def.setting(
    COMPILE(
      "org.testcontainers" % "oracle-xe" % testcontainersVersion
    ) ++ TEST(
      "com.oracle.ojdbc" % "ojdbc8" % oracleDriverVersion
    )
  )

  val moduleCassandra = Def.setting(
    COMPILE(
      "org.testcontainers" % "cassandra" % testcontainersVersion
    ) ++ TEST(
      "com.datastax.oss" % "java-driver-core" % cassandraDriverVersion
    )
  )

  val moduleKafka = Def.setting(
    COMPILE(
      "org.testcontainers" % "kafka" % testcontainersVersion
    ) ++ TEST(
      "org.apache.kafka" % "kafka-clients" % kafkaDriverVersion
    )
  )

  val moduleVault = Def.setting(
    COMPILE(
      "org.testcontainers" % "vault" % testcontainersVersion
    ) ++ TEST(
      "io.rest-assured" % "scala-support" % restAssuredVersion
    )
  )

  val moduleMssqlserver = Def.setting(
    COMPILE(
      "org.testcontainers" % "mssqlserver" % testcontainersVersion
    )
  )

  val moduleClickhouse = Def.setting(
    COMPILE(
      "org.testcontainers" % "clickhouse" % testcontainersVersion
    ) ++ TEST(
      "ru.yandex.clickhouse" % "clickhouse-jdbc" % "0.2.4",
    )
  )

  val moduleCockroachdb = Def.setting(
    COMPILE(
      "org.testcontainers" % "cockroachdb" % testcontainersVersion
    )
  )

  val moduleCouchbase = Def.setting(
    COMPILE(
      "org.testcontainers" % "couchbase" % testcontainersVersion
    )
  )

  val moduleDb2 = Def.setting(
    COMPILE(
      "org.testcontainers" % "db2" % testcontainersVersion
    )
  )

  val moduleDynalite = Def.setting(
    COMPILE(
      "org.testcontainers" % "dynalite" % testcontainersVersion
    ) ++ PROVIDED(
      "com.amazonaws" % "aws-java-sdk-dynamodb" % awsVersion
    )
  )

  val moduleElasticsearch = Def.setting(
    COMPILE(
      "org.testcontainers" % "elasticsearch" % testcontainersVersion
    )
  )

  val moduleInfluxdb = Def.setting(
    COMPILE(
      "org.testcontainers" % "influxdb" % testcontainersVersion
    ) ++ PROVIDED(
      "org.influxdb" % "influxdb-java" % "2.17"
    )
  )

  val moduleLocalstack = Def.setting(
    COMPILE(
      "org.testcontainers" % "localstack" % testcontainersVersion
    ) ++ PROVIDED(
      "com.amazonaws" % "aws-java-sdk-s3" % awsVersion
    )
  )

  val moduleMariadb = Def.setting(
    COMPILE(
      "org.testcontainers" % "mariadb" % testcontainersVersion
    )
  )

  val moduleMockserver = Def.setting(
    COMPILE(
      "org.testcontainers" % "mockserver" % testcontainersVersion
    )
  )

  val moduleNginx = Def.setting(
    COMPILE(
      "org.testcontainers" % "nginx" % testcontainersVersion
    )
  )

  val modulePulsar = Def.setting(
    COMPILE(
      "org.testcontainers" % "pulsar" % testcontainersVersion
    )
  )

  val moduleRabbitmq = Def.setting(
    COMPILE(
      "org.testcontainers" % "rabbitmq" % testcontainersVersion
    )
  )

  val moduleToxiproxy = Def.setting(
    COMPILE(
      "org.testcontainers" % "toxiproxy" % testcontainersVersion
    )
  )
}
