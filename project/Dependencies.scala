import sbt._

object Dependencies {
  private def COMPILE(modules: sbt.ModuleID*): Seq[sbt.ModuleID] = deps(None, modules: _*)

  private def PROVIDED(modules: sbt.ModuleID*): Seq[sbt.ModuleID] = deps(Some("provided"), modules: _*)

  private def TEST(modules: sbt.ModuleID*): Seq[sbt.ModuleID] = deps(Some("test"), modules: _*)

  private def deps(scope: Option[String], modules: sbt.ModuleID*): Seq[sbt.ModuleID] = {
    scope.map(s => modules.map(_ % s)).getOrElse(modules)
  }

  private val testcontainersVersion = "1.12.2"
  private val seleniumVersion = "2.53.1"
  private val slf4jVersion = "1.7.25"
  private val scalaTestVersion = "3.0.8"
  private val mysqlConnectorVersion = "5.1.42"
  private val cassandraDriverVersion = "4.0.1"
  private val postgresqlDriverVersion = "9.4.1212"
  private val kafkaDriverVersion = "2.2.0"
  private val mockitoVersion = "2.27.0"
  private val restAssuredVersion = "4.0.0"

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

  val moduleMysql = Def.setting(
    COMPILE(
      "org.testcontainers" % "mysql" % testcontainersVersion
    ) ++ TEST(
      "mysql" % "mysql-connector-java" % mysqlConnectorVersion
    )
  )

  val modulePostgres = Def.setting(
    COMPILE(
      "org.testcontainers" % "postgresql" % testcontainersVersion
    ) ++ TEST(
      "org.postgresql" % "postgresql" % postgresqlDriverVersion
    )
  )

  val moduleCassandra = Def.setting(
    COMPILE(
      "org.testcontainers" % "cassandra" % testcontainersVersion,
    ) ++ TEST(
      "com.datastax.oss" % "java-driver-core" % cassandraDriverVersion,
    )
  )

  val moduleKafka = Def.setting(
    COMPILE(
      "org.testcontainers" % "kafka" % testcontainersVersion
    ) ++ TEST(
      "org.apache.kafka" % "kafka-clients" % kafkaDriverVersion,
    )
  )

  val moduleVault = Def.setting(
    COMPILE(
      "org.testcontainers" % "vault" % testcontainersVersion,
    ) ++ TEST(
      "io.rest-assured" % "scala-support" % restAssuredVersion
    )
  )
}
