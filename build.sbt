import xerial.sbt.Sonatype.*
import ReleaseTransformations.*
import java.net.URI

lazy val compileScalastyle = taskKey[Unit]("compileScalastyle")

val commonSettings = Seq(
  ThisBuild / scalaVersion := "2.12.15",
  crossScalaVersions := Seq("2.11.12", "2.12.15", "2.13.8", "3.1.2"),

  ThisBuild / parallelExecution := false,
  fork := true,

  scalacOptions ++= {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((3, _)) =>
        Seq(
          "-unchecked",
          "-deprecation",
          "-language:_",
          "-encoding", "UTF-8",
          "-source:3.0-migration"
        )
      case _ =>
        Seq(
          "-unchecked",
          "-deprecation",
          "-language:_",
          "-target:jvm-1.8",
          "-encoding", "UTF-8"
        )
    }
  },

  /**
   * Publishing
   */
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value) {
      Some("snapshots" at nexus + "content/repositories/snapshots")
    } else {
      Some("releases" at nexus + "service/local/staging/deploy/maven2")
    }
  },
  publishMavenStyle := true,
  sonatypeProfileName := "testcontainers-scala",
  sonatypeProjectHosting := Some(GitHubHosting("testcontainers", "testcontainers-scala", "dimafeng@gmail.com")),
  licenses := Seq("The MIT License (MIT)" -> URI.create("https://opensource.org/licenses/MIT").toURL),
  ThisBuild / organization := "com.dimafeng",

  Global / parallelExecution := false,

  releaseCrossBuild := true
)


lazy val noPublishSettings = Seq(
  publish / skip  := true
)

lazy val root = (project in file("."))
  .aggregate(
    core,
    munit,
    scalatest,
    scalatestSelenium,
    jdbc,
    moduleMysql,
    moduleNeo4j,
    modulePostgres,
    moduleOracle,
    moduleCassandra,
    moduleKafka,
    moduleVault,
    moduleMssqlserver,
    moduleClickhouse,
    moduleCockroachdb,
    moduleCouchbase,
    moduleDb2,
    moduleDynalite,
    moduleElasticsearch,
    moduleInfluxdb,
    moduleLocalstack,
    moduleLocalstackV2,
    moduleMariadb,
    moduleMockserver,
    moduleNginx,
    modulePulsar,
    moduleRabbitmq,
    moduleToxiproxy,
    moduleOrientdb,
    modulePresto,
    moduleTrino,
    moduleMongodb,
    moduleSolr,
    moduleGcloud,
    moduleRedpanda,
    allOld
  )
  .settings(noPublishSettings)
  .settings(
    releaseProcess := Seq[ReleaseStep](
      checkSnapshotDependencies,
      inquireVersions,
      runClean,
      //runTest,
      setReleaseVersion,
      commitReleaseVersion,
      tagRelease,
      releaseStepCommandAndRemaining("+publishSigned"),
      setNextVersion,
      commitNextVersion,
      releaseStepCommand("sonatypeReleaseAll"),
      pushChanges
    )
  )

lazy val allOld = (project in file("allOld"))
  .dependsOn(
    core,
    scalatest,
    scalatestSelenium,
    moduleMysql,
    modulePostgres,
    moduleCassandra,
    moduleKafka,
    moduleVault,
  )
  .settings(commonSettings)
  .settings(
    name := "testcontainers-scala",
    libraryDependencies ++= Dependencies.allOld.value ++ (
      Dependencies.scalatestSelenium.value ++
        Dependencies.moduleMysql.value ++
        Dependencies.modulePostgres.value ++
        Dependencies.moduleCassandra.value ++
        Dependencies.moduleKafka.value ++
        Dependencies.moduleVault.value
      ).collect {
      case module if module.configurations.isEmpty =>
        module.withConfigurations(Some("provided"))
    },
    pomPostProcess := { root =>
      import scala.xml.{Node => XmlNode, NodeSeq => XmlNodeSeq, _}
      import scala.xml.transform.{RewriteRule, RuleTransformer}

      class ExcludeModule(moduleName: String) {
        def unapply(e: Elem): Option[Elem] = {
          if (
            e.label == "dependency" &&
              e.child.exists(c => c.label == "groupId" && c.text == "com.dimafeng") &&
              e.child.exists(c => c.label == "artifactId" && c.text.startsWith(moduleName))
          ) {
            Some(e)
          } else {
            None
          }
        }
      }

      val scalatestSeleniumEx = new ExcludeModule((scalatestSelenium/name).value)
      val moduleMysqlEx       = new ExcludeModule((moduleMysql/name).value)
      val modulePostgresEx    = new ExcludeModule((modulePostgres/name).value)
      val moduleCassandraEx   = new ExcludeModule((moduleCassandra/name).value)
      val moduleKafkaEx       = new ExcludeModule((moduleKafka/name).value)
      val moduleVaultEx       = new ExcludeModule((moduleVault/name).value)

      def exclude(modules: Seq[ModuleID]): Elem = {
        <exclusions>
          {
          modules.map { module =>
            if (module.configurations.isEmpty) {
              <exclusion>
                <groupId>{module.organization}</groupId>
                <artifactId>{module.name}</artifactId>
              </exclusion>
            } else {
              XmlNodeSeq.Empty
            }
          }
          }
        </exclusions>
      }

      new RuleTransformer(new RewriteRule {
        override def transform(node: XmlNode): XmlNodeSeq = node match {
          case scalatestSeleniumEx(e) =>
            e.copy(child = e.child :+ exclude(Dependencies.scalatestSelenium.value))

          case moduleMysqlEx(e) =>
            e.copy(child = e.child :+ exclude(Dependencies.moduleMysql.value))

          case modulePostgresEx(e) =>
            e.copy(child = e.child :+ exclude(Dependencies.modulePostgres.value))

          case moduleCassandraEx(e) =>
            e.copy(child = e.child :+ exclude(Dependencies.moduleCassandra.value))

          case moduleKafkaEx(e) =>
            e.copy(child = e.child :+ exclude(Dependencies.moduleKafka.value))

          case moduleVaultEx(e) =>
            e.copy(child = e.child :+ exclude(Dependencies.moduleVault.value))

          case _ =>
            node
        }
      }).transform(root).head
    }
  )

lazy val core = (project in file("core"))
  .settings(commonSettings)
  .settings(
    name := "testcontainers-scala-core",
    libraryDependencies ++= Dependencies.core.value
  )

lazy val scalatest = (project in file("test-framework/scalatest"))
  .dependsOn(core % "compile->compile;test->test;provided->provided")
  .settings(commonSettings)
  .settings(
    name := "testcontainers-scala-scalatest",
    libraryDependencies ++= Dependencies.scalatest.value
  )

lazy val munit = (project in file("test-framework/munit"))
  .dependsOn(core % "compile->compile;test->test;provided->provided")
  .settings(commonSettings)
  .settings(testFrameworks += new TestFramework("munit.Framework"))
  .settings(
    name := "testcontainers-scala-munit",
    libraryDependencies ++= Dependencies.munit.value
  )

lazy val scalatestSelenium = (project in file("test-framework/scalatest-selenium"))
  .dependsOn(scalatest % "compile->compile;test->test;provided->provided")
  .settings(commonSettings)
  .settings(
    name := "testcontainers-scala-scalatest-selenium",
    libraryDependencies ++= Dependencies.scalatestSelenium.value
  )

lazy val jdbc = (project in file("modules/jdbc"))
  .dependsOn(core % "compile->compile;test->test;provided->provided")
  .settings(commonSettings)
  .settings(
    name := "testcontainers-scala-jdbc",
    libraryDependencies ++= Dependencies.jdbc.value
  )

lazy val moduleMysql = (project in file("modules/mysql"))
  .dependsOn(core % "compile->compile;test->test;provided->provided", scalatest % "test->test", jdbc)
  .settings(commonSettings)
  .settings(
    name := "testcontainers-scala-mysql",
    libraryDependencies ++= Dependencies.moduleMysql.value
  )

lazy val moduleNeo4j = (project in file("modules/neo4j"))
  .dependsOn(core % "compile->compile;test->test;provided->provided", scalatest % "test->test")
  .settings(commonSettings)
  .settings(
    name := "testcontainers-scala-neo4j",
    libraryDependencies ++= Dependencies.moduleNeo4j.value
  )

lazy val modulePostgres = (project in file("modules/postgres"))
  .dependsOn(core % "compile->compile;test->test;provided->provided", scalatest % "test->test", jdbc)
  .settings(commonSettings)
  .settings(
    name := "testcontainers-scala-postgresql",
    libraryDependencies ++= Dependencies.modulePostgres.value
  )

lazy val moduleOracle = (project in file("modules/oracle"))
  .dependsOn(core % "compile->compile;test->test;provided->provided", scalatest % "test->test", jdbc)
  .settings(commonSettings)
  .settings(
    name := "testcontainers-scala-oracle-xe",
    libraryDependencies ++= Dependencies.moduleOracle.value
  )

lazy val moduleCassandra = (project in file("modules/cassandra"))
  .dependsOn(core % "compile->compile;test->test;provided->provided", scalatest % "test->test")
  .settings(commonSettings)
  .settings(
    name := "testcontainers-scala-cassandra",
    libraryDependencies ++= Dependencies.moduleCassandra.value
  )

lazy val moduleKafka = (project in file("modules/kafka"))
  .dependsOn(core % "compile->compile;test->test;provided->provided", scalatest % "test->test")
  .settings(commonSettings)
  .settings(
    name := "testcontainers-scala-kafka",
    libraryDependencies ++= Dependencies.moduleKafka.value
  )

lazy val moduleVault = (project in file("modules/vault"))
  .dependsOn(core % "compile->compile;test->test;provided->provided", scalatest % "test->test")
  .settings(commonSettings)
  .settings(
    name := "testcontainers-scala-vault",
    libraryDependencies ++= Dependencies.moduleVault.value
  )

lazy val moduleMssqlserver = (project in file("modules/mssqlserver"))
  .dependsOn(core % "compile->compile;test->test;provided->provided", jdbc)
  .settings(commonSettings)
  .settings(
    name := "testcontainers-scala-mssqlserver",
    libraryDependencies ++= Dependencies.moduleMssqlserver.value
  )

lazy val moduleClickhouse = (project in file("modules/clickhouse"))
  .dependsOn(scalatest % "test->test", jdbc)
  .settings(commonSettings)
  .settings(
    name := "testcontainers-scala-clickhouse",
    libraryDependencies ++= Dependencies.moduleClickhouse.value
  )

lazy val moduleCockroachdb = (project in file("modules/cockroachdb"))
  .dependsOn(core % "compile->compile;test->test;provided->provided", jdbc)
  .settings(commonSettings)
  .settings(
    name := "testcontainers-scala-cockroachdb",
    libraryDependencies ++= Dependencies.moduleCockroachdb.value
  )

lazy val moduleCouchbase = (project in file("modules/couchbase"))
  .dependsOn(core % "compile->compile;test->test;provided->provided")
  .settings(commonSettings)
  .settings(
    name := "testcontainers-scala-couchbase",
    libraryDependencies ++= Dependencies.moduleCouchbase.value
  )

lazy val moduleDb2 = (project in file("modules/db2"))
  .dependsOn(core % "compile->compile;test->test;provided->provided", jdbc)
  .settings(commonSettings)
  .settings(
    name := "testcontainers-scala-db2",
    libraryDependencies ++= Dependencies.moduleDb2.value
  )

lazy val moduleDynalite = (project in file("modules/dynalite"))
  .dependsOn(core % "compile->compile;test->test;provided->provided")
  .settings(commonSettings)
  .settings(
    name := "testcontainers-scala-dynalite",
    libraryDependencies ++= Dependencies.moduleDynalite.value
  )

lazy val moduleElasticsearch = (project in file("modules/elasticsearch"))
  .dependsOn(core % "compile->compile;test->test;provided->provided")
  .settings(commonSettings)
  .settings(
    name := "testcontainers-scala-elasticsearch",
    libraryDependencies ++= Dependencies.moduleElasticsearch.value
  )

lazy val moduleInfluxdb = (project in file("modules/influxdb"))
  .dependsOn(core % "compile->compile;test->test;provided->provided")
  .settings(commonSettings)
  .settings(
    name := "testcontainers-scala-influxdb",
    libraryDependencies ++= Dependencies.moduleInfluxdb.value
  )

lazy val moduleLocalstack = (project in file("modules/localstack"))
  .dependsOn(core % "compile->compile;test->test;provided->provided")
  .settings(commonSettings)
  .settings(
    name := "testcontainers-scala-localstack",
    libraryDependencies ++= Dependencies.moduleLocalstack.value
  )

lazy val moduleLocalstackV2 = (project in file("modules/localstackV2"))
  .dependsOn(core % "compile->compile;test->test;provided->provided")
  .settings(commonSettings)
  .settings(
    name := "testcontainers-scala-localstack-v2",
    libraryDependencies ++= Dependencies.moduleLocalstackV2.value
  )

lazy val moduleMariadb = (project in file("modules/mariadb"))
  .dependsOn(core % "compile->compile;test->test;provided->provided", jdbc)
  .settings(commonSettings)
  .settings(
    name := "testcontainers-scala-mariadb",
    libraryDependencies ++= Dependencies.moduleMariadb.value
  )

lazy val moduleMockserver = (project in file("modules/mockserver"))
  .dependsOn(core % "compile->compile;test->test;provided->provided")
  .settings(commonSettings)
  .settings(
    name := "testcontainers-scala-mockserver",
    libraryDependencies ++= Dependencies.moduleMockserver.value
  )

lazy val moduleNginx = (project in file("modules/nginx"))
  .dependsOn(core % "compile->compile;test->test;provided->provided")
  .settings(commonSettings)
  .settings(
    name := "testcontainers-scala-nginx",
    libraryDependencies ++= Dependencies.moduleNginx.value
  )

lazy val modulePulsar = (project in file("modules/pulsar"))
  .dependsOn(core % "compile->compile;test->test;provided->provided")
  .settings(commonSettings)
  .settings(
    name := "testcontainers-scala-pulsar",
    libraryDependencies ++= Dependencies.modulePulsar.value
  )

lazy val moduleRabbitmq = (project in file("modules/rabbitmq"))
  .dependsOn(core % "compile->compile;test->test;provided->provided", scalatest % "test->test")
  .settings(commonSettings)
  .settings(
    name := "testcontainers-scala-rabbitmq",
    libraryDependencies ++= Dependencies.moduleRabbitmq.value
  )

lazy val moduleToxiproxy = (project in file("modules/toxiproxy"))
  .dependsOn(core % "compile->compile;test->test;provided->provided", scalatest % "test->test")
  .settings(commonSettings)
  .settings(
    name := "testcontainers-scala-toxiproxy",
    libraryDependencies ++= Dependencies.moduleToxiproxy.value
  )

lazy val moduleOrientdb = (project in file("modules/orientdb"))
  .dependsOn(core % "compile->compile;test->test;provided->provided")
  .settings(commonSettings)
  .settings(
    name := "testcontainers-scala-orientdb",
    libraryDependencies ++= Dependencies.moduleOrientdb.value
  )

lazy val modulePresto = (project in file("modules/presto"))
  .dependsOn(core % "compile->compile;test->test;provided->provided", jdbc)
  .settings(commonSettings)
  .settings(
    name := "testcontainers-scala-presto",
    libraryDependencies ++= Dependencies.modulePresto.value
  )

lazy val moduleTrino = (project in file("modules/trino"))
  .dependsOn(core % "compile->compile;test->test;provided->provided", jdbc)
  .settings(commonSettings)
  .settings(
    name := "testcontainers-scala-trino",
    libraryDependencies ++= Dependencies.moduleTrino.value
  )

lazy val moduleMongodb = (project in file("modules/mongodb"))
  .dependsOn(core % "compile->compile;test->test;provided->provided", jdbc, scalatest % "test->test")
  .settings(commonSettings)
  .settings(
    name := "testcontainers-scala-mongodb",
    libraryDependencies ++= Dependencies.moduleMongodb.value
  )

lazy val moduleSolr = (project in file("modules/solr"))
  .dependsOn(core % "compile->compile;test->test;provided->provided", jdbc)
  .settings(commonSettings)
  .settings(
    name := "testcontainers-scala-solr",
    libraryDependencies ++= Dependencies.moduleSolr.value
  )

lazy val moduleGcloud = (project in file("modules/gcloud"))
  .dependsOn(core, scalatest % "test->test")
  .settings(commonSettings)
  .settings(
    name := "testcontainers-scala-gcloud",
    libraryDependencies ++= Dependencies.moduleGcloud.value
  )

lazy val moduleRedpanda = (project in file("modules/redpanda"))
  .dependsOn(core % "compile->compile;test->test;provided->provided", scalatest % "test->test")
  .settings(commonSettings)
  .settings(
    name := "testcontainers-scala-redpanda",
    libraryDependencies ++= Dependencies.moduleRedpanda.value
  )
