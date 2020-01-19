import xerial.sbt.Sonatype._
import ReleaseTransformations._

lazy val compileScalastyle = taskKey[Unit]("compileScalastyle")

val commonSettings = Seq(
  scalaVersion in ThisBuild := "2.12.9",
  crossScalaVersions := Seq("2.11.12", "2.12.9", "2.13.1"),

  parallelExecution in ThisBuild := false,
  fork := true,

  scalacOptions ++= Seq(
    "-unchecked",
    "-deprecation",
    "-language:_",
    "-target:jvm-1.8",
    "-encoding", "UTF-8"
  ),

  /**
    * Publishing
    */
  useGpg := true,
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases" at nexus + "service/local/staging/deploy/maven2")
  },
  publishMavenStyle := true,
  sonatypeProfileName := "testcontainers-scala",
  sonatypeProjectHosting := Some(GitLabHosting("testcontainers", "testcontainers-scala", "dimafeng@gmail.com")),
  licenses := Seq("The MIT License (MIT)" -> new URL("https://opensource.org/licenses/MIT")),
  organization in ThisBuild := "com.dimafeng",

  parallelExecution in Global := false,

  releaseCrossBuild := true
)

lazy val noPublishSettings = Seq(
  skip in publish := true
)

lazy val root = (project in file("."))
  .aggregate(
    core,
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
    moduleMariadb,
    moduleMockserver,
    moduleNginx,
    modulePulsar,
    moduleRabbitmq,
    moduleToxiproxy,
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
  .settings(commonSettings: _*)
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
          ) Some(e) else None
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
  .settings(commonSettings: _*)
  .settings(
    name := "testcontainers-scala-core",
    libraryDependencies ++= Dependencies.core.value
  )

lazy val scalatest = (project in file("test-framework/scalatest"))
  .dependsOn(core % "compile->compile;test->test;provided->provided")
  .settings(commonSettings: _*)
  .settings(
    name := "testcontainers-scala-scalatest",
    libraryDependencies ++= Dependencies.scalatest.value
  )

lazy val scalatestSelenium = (project in file("test-framework/scalatest-selenium"))
  .dependsOn(scalatest % "compile->compile;test->test;provided->provided")
  .settings(commonSettings: _*)
  .settings(
    name := "testcontainers-scala-scalatest-selenium",
    libraryDependencies ++= Dependencies.scalatestSelenium.value
  )

lazy val jdbc = (project in file("modules/jdbc"))
  .dependsOn(scalatest % "compile->compile;test->test;provided->provided")
  .settings(commonSettings: _*)
  .settings(
    name := "testcontainers-scala-jdbc",
    libraryDependencies ++= Dependencies.jdbc.value
  )

lazy val moduleMysql = (project in file("modules/mysql"))
  .dependsOn(scalatest % "compile->compile;test->test;provided->provided", jdbc)
  .settings(commonSettings: _*)
  .settings(
    name := "testcontainers-scala-mysql",
    libraryDependencies ++= Dependencies.moduleMysql.value
  )

lazy val moduleNeo4j = (project in file("modules/neo4j"))
  .dependsOn(scalatest % "compile->compile;test->test;provided->provided")
  .settings(commonSettings: _*)
  .settings(
    name := "testcontainers-scala-neo4j",
    libraryDependencies ++= Dependencies.moduleNeo4j.value
  )

lazy val modulePostgres = (project in file("modules/postgres"))
  .dependsOn(scalatest % "compile->compile;test->test;provided->provided", jdbc)
  .settings(commonSettings: _*)
  .settings(
    name := "testcontainers-scala-postgresql",
    libraryDependencies ++= Dependencies.modulePostgres.value
  )

lazy val moduleOracle = (project in file("modules/oracle"))
  .dependsOn(scalatest % "compile->compile;test->test;provided->provided", jdbc)
  .settings(commonSettings: _*)
  .settings(
    name := "testcontainers-scala-oracle-xe",
    libraryDependencies ++= Dependencies.moduleOracle.value
  )

lazy val moduleCassandra = (project in file("modules/cassandra"))
  .dependsOn(scalatest % "compile->compile;test->test;provided->provided")
  .settings(commonSettings: _*)
  .settings(
    name := "testcontainers-scala-cassandra",
    libraryDependencies ++= Dependencies.moduleCassandra.value
  )

lazy val moduleKafka = (project in file("modules/kafka"))
  .dependsOn(scalatest % "compile->compile;test->test;provided->provided")
  .settings(commonSettings: _*)
  .settings(
    name := "testcontainers-scala-kafka",
    libraryDependencies ++= Dependencies.moduleKafka.value
  )

lazy val moduleVault = (project in file("modules/vault"))
  .dependsOn(scalatest % "compile->compile;test->test;provided->provided")
  .settings(commonSettings: _*)
  .settings(
    name := "testcontainers-scala-vault",
    libraryDependencies ++= Dependencies.moduleVault.value
  )

lazy val moduleMssqlserver = (project in file("modules/mssqlserver"))
  .dependsOn(scalatest % "compile->compile;test->test;provided->provided", jdbc)
  .settings(commonSettings: _*)
  .settings(
    name := "testcontainers-scala-mssqlserver",
    libraryDependencies ++= Dependencies.moduleMssqlserver.value
  )

lazy val moduleClickhouse = (project in file("modules/clickhouse"))
  .dependsOn(scalatest % "compile->compile;test->test;provided->provided", jdbc)
  .settings(commonSettings: _*)
  .settings(
    name := "testcontainers-scala-clickhouse",
    libraryDependencies ++= Dependencies.moduleClickhouse.value
  )

lazy val moduleCockroachdb = (project in file("modules/cockroachdb"))
  .dependsOn(scalatest % "compile->compile;test->test;provided->provided", jdbc)
  .settings(commonSettings: _*)
  .settings(
    name := "testcontainers-scala-cockroachdb",
    libraryDependencies ++= Dependencies.moduleCockroachdb.value
  )

lazy val moduleCouchbase = (project in file("modules/couchbase"))
  .dependsOn(scalatest % "compile->compile;test->test;provided->provided")
  .settings(commonSettings: _*)
  .settings(
    name := "testcontainers-scala-couchbase",
    libraryDependencies ++= Dependencies.moduleCouchbase.value
  )

lazy val moduleDb2 = (project in file("modules/db2"))
  .dependsOn(scalatest % "compile->compile;test->test;provided->provided", jdbc)
  .settings(commonSettings: _*)
  .settings(
    name := "testcontainers-scala-db2",
    libraryDependencies ++= Dependencies.moduleDb2.value
  )

lazy val moduleDynalite = (project in file("modules/dynalite"))
  .dependsOn(scalatest % "compile->compile;test->test;provided->provided")
  .settings(commonSettings: _*)
  .settings(
    name := "testcontainers-scala-dynalite",
    libraryDependencies ++= Dependencies.moduleDynalite.value
  )

lazy val moduleElasticsearch = (project in file("modules/elasticsearch"))
  .dependsOn(scalatest % "compile->compile;test->test;provided->provided")
  .settings(commonSettings: _*)
  .settings(
    name := "testcontainers-scala-elasticsearch",
    libraryDependencies ++= Dependencies.moduleElasticsearch.value
  )

lazy val moduleInfluxdb = (project in file("modules/influxdb"))
  .dependsOn(scalatest % "compile->compile;test->test;provided->provided")
  .settings(commonSettings: _*)
  .settings(
    name := "testcontainers-scala-influxdb",
    libraryDependencies ++= Dependencies.moduleInfluxdb.value
  )

lazy val moduleLocalstack = (project in file("modules/localstack"))
  .dependsOn(scalatest % "compile->compile;test->test;provided->provided")
  .settings(commonSettings: _*)
  .settings(
    name := "testcontainers-scala-localstack",
    libraryDependencies ++= Dependencies.moduleLocalstack.value
  )

lazy val moduleMariadb = (project in file("modules/mariadb"))
  .dependsOn(scalatest % "compile->compile;test->test;provided->provided", jdbc)
  .settings(commonSettings: _*)
  .settings(
    name := "testcontainers-scala-mariadb",
    libraryDependencies ++= Dependencies.moduleMariadb.value
  )

lazy val moduleMockserver = (project in file("modules/mockserver"))
  .dependsOn(scalatest % "compile->compile;test->test;provided->provided")
  .settings(commonSettings: _*)
  .settings(
    name := "testcontainers-scala-mockserver",
    libraryDependencies ++= Dependencies.moduleMockserver.value
  )

lazy val moduleNginx = (project in file("modules/nginx"))
  .dependsOn(scalatest % "compile->compile;test->test;provided->provided")
  .settings(commonSettings: _*)
  .settings(
    name := "testcontainers-scala-nginx",
    libraryDependencies ++= Dependencies.moduleNginx.value
  )

lazy val modulePulsar = (project in file("modules/pulsar"))
  .dependsOn(scalatest % "compile->compile;test->test;provided->provided")
  .settings(commonSettings: _*)
  .settings(
    name := "testcontainers-scala-pulsar",
    libraryDependencies ++= Dependencies.modulePulsar.value
  )

lazy val moduleRabbitmq = (project in file("modules/rabbitmq"))
  .dependsOn(scalatest % "compile->compile;test->test;provided->provided")
  .settings(commonSettings: _*)
  .settings(
    name := "testcontainers-scala-rabbitmq",
    libraryDependencies ++= Dependencies.moduleRabbitmq.value
  )

lazy val moduleToxiproxy = (project in file("modules/toxiproxy"))
  .dependsOn(scalatest % "compile->compile;test->test;provided->provided")
  .settings(commonSettings: _*)
  .settings(
    name := "testcontainers-scala-toxiproxy",
    libraryDependencies ++= Dependencies.moduleToxiproxy.value
  )

lazy val microsite = (project in file("docs"))
  .settings(moduleName := "docs")
  .enablePlugins(MicrositesPlugin)
  .settings(
    micrositeName := "testcontainers-scala",
    micrositeDescription := "Docker containers for testing in scala ",
    micrositeAuthor := "dimafeng",
    micrositeHighlightTheme := "atom-one-light",
    micrositeHomepage := "https://github.com/testcontainers/testcontainers-scala",
    micrositeDocumentationUrl := "docs.html",
    micrositeGithubOwner := "testcontainers",
    micrositeGithubRepo := "testcontainers-scala",
    micrositeBaseUrl := "/testcontainers-scala",
    ghpagesNoJekyll := false,
    fork in tut := true
  )
