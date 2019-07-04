import Dependencies.{PROVIDED, TEST, _}
import xerial.sbt.Sonatype._
import ReleaseTransformations._

val testcontainersVersion = "1.11.3"
val seleniumVersion = "2.53.1"
val slf4jVersion = "1.7.25"
val scalaTestVersion = "3.0.7"
val mysqlConnectorVersion = "5.1.42"
val cassandraDriverVersion = "4.0.1"
val postgresqlDriverVersion = "9.4.1212"
val kafkaDriverVersion = "2.2.0"
val mockitoVersion = "2.27.0"
val restAssuredVersion = "4.0.0"

lazy val compileScalastyle = taskKey[Unit]("compileScalastyle")

val commonSettings = Seq(
  scalaVersion in ThisBuild := "2.12.8",
  crossScalaVersions := Seq("2.11.12", "2.12.8", "2.13.0-M5"),

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
    moduleMysql,
    modulePostgres
  )
  //.settings(noPublishSettings)
  .settings(
    name := "testcontainers-scala",

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

lazy val core = (project in file("core"))
  .settings(commonSettings: _*)
  .settings(
    name := "testcontainers-scala-core",

    libraryDependencies ++=
      COMPILE(
        "org.testcontainers" % "testcontainers" % testcontainersVersion
      )
        ++ PROVIDED(
        "org.slf4j" % "slf4j-simple" % slf4jVersion
      )
        ++ TEST(
        "junit" % "junit" % "4.12",
        "org.scalatest" %% "scalatest" % scalaTestVersion,
        "org.testcontainers" % "selenium" % testcontainersVersion,
        "org.postgresql" % "postgresql" % postgresqlDriverVersion,
        "org.mockito" % "mockito-core" % mockitoVersion
      )
  )

lazy val scalatest = (project in file("test-framework/scalatest"))
  .dependsOn(core % "compile->compile;test->test;provided->provided")
  .settings(commonSettings: _*)
  .settings(
    name := "testcontainers-scala-scalatest",
    libraryDependencies ++= PROVIDED(
      "org.scalatest" %% "scalatest" % scalaTestVersion
    )
  )

lazy val scalatestSelenium = (project in file("test-framework/scalatest-selenium"))
  .dependsOn(scalatest % "compile->compile;test->test;provided->provided")
  .settings(commonSettings: _*)
  .settings(
    name := "testcontainers-scala-scalatest-selenium",
    libraryDependencies ++= COMPILE(
      "org.testcontainers" % "selenium" % testcontainersVersion,
      "org.seleniumhq.selenium" % "selenium-java" % seleniumVersion
    )
  )

lazy val moduleMysql = (project in file("modules/mysql"))
  .dependsOn(scalatest % "compile->compile;test->test;provided->provided")
  .settings(commonSettings: _*)
  .settings(
    name := "testcontainers-scala-mysql",
    libraryDependencies ++= COMPILE(
      "org.testcontainers" % "mysql" % testcontainersVersion
    ) ++ TEST(
      "mysql" % "mysql-connector-java" % mysqlConnectorVersion
    )
  )

lazy val modulePostgres = (project in file("modules/postgres"))
  .dependsOn(scalatest % "compile->compile;test->test;provided->provided")
  .settings(commonSettings: _*)
  .settings(
    name := "testcontainers-scala-mysql",
    libraryDependencies ++= COMPILE(
      "org.testcontainers" % "postgresql" % testcontainersVersion
    ) ++ TEST(
      "org.postgresql" % "postgresql" % postgresqlDriverVersion
    )
  )

lazy val moduleCassandra = (project in file("modules/cassandra"))
  .dependsOn(scalatest % "compile->compile;test->test;provided->provided")
  .settings(commonSettings: _*)
  .settings(
    name := "testcontainers-scala-cassandra",
    libraryDependencies ++= COMPILE(
      "org.testcontainers" % "cassandra" % testcontainersVersion,
    ) ++ TEST(
      "com.datastax.oss" % "java-driver-core" % cassandraDriverVersion,
    )
  )

lazy val moduleKafka = (project in file("modules/kafka"))
  .dependsOn(scalatest % "compile->compile;test->test;provided->provided")
  .settings(commonSettings: _*)
  .settings(
    name := "testcontainers-scala-kafka",
    libraryDependencies ++= COMPILE(
      "org.testcontainers" % "kafka" % testcontainersVersion
    ) ++ TEST(
      "org.apache.kafka" % "kafka-clients" % kafkaDriverVersion,
    )
  )

lazy val moduleVault = (project in file("modules/vault"))
  .dependsOn(scalatest % "compile->compile;test->test;provided->provided")
  .settings(commonSettings: _*)
  .settings(
    name := "testcontainers-scala-kafka",
    libraryDependencies ++= COMPILE(
      "org.testcontainers" % "vault" % testcontainersVersion,
    ) ++ TEST(
      "org.apache.kafka" % "kafka-clients" % kafkaDriverVersion,
    )
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
