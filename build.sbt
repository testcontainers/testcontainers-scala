import Dependencies._
import xerial.sbt.Sonatype._
import ReleaseTransformations._

val testcontainersVersion = "1.11.4"
val seleniumVersion = "2.53.1"
val slf4jVersion = "1.7.25"
val scalaTestVersion = "3.0.8"
val mysqlConnectorVersion = "5.1.42"
val cassandraDriverVersion = "4.0.1"
val postgresqlDriverVersion = "9.4.1212"
val kafkaDriverVersion = "2.2.0"
val mockitoVersion = "2.27.0"
val restAssuredVersion = "4.0.0"

lazy val compileScalastyle = taskKey[Unit]("compileScalastyle")

lazy val root = (project in file("."))
  .settings(
    organization in ThisBuild := "com.dimafeng",
    scalaVersion in ThisBuild := "2.12.8",
    crossScalaVersions := Seq("2.11.12", "2.12.8", "2.13.0"),
    name := "testcontainers-scala",
    scalacOptions ++= Seq(
      "-unchecked",
      "-deprecation",
      "-language:_",
      "-target:jvm-1.8",
      "-encoding", "UTF-8"
    ),
    compileScalastyle := scalastyle.in(Compile).toTask("").value,
    test in Test := (test in Test).dependsOn(compileScalastyle in Compile).value,

    /**
      * Dependencies
      */
    libraryDependencies ++=
      COMPILE(
        "org.testcontainers" % "testcontainers" % testcontainersVersion
      )
        ++ PROVIDED(
        "org.seleniumhq.selenium" % "selenium-java" % seleniumVersion,
        "org.testcontainers" % "selenium" % testcontainersVersion,
        "org.slf4j" % "slf4j-simple" % slf4jVersion,
        "org.scalatest" %% "scalatest" % scalaTestVersion,
        "org.testcontainers" % "cassandra" % testcontainersVersion,
        "org.testcontainers" % "mysql" % testcontainersVersion,
        "org.testcontainers" % "postgresql" % testcontainersVersion,
        "org.testcontainers" % "kafka" % testcontainersVersion,
        "org.testcontainers" % "vault" % testcontainersVersion,
      )
        ++ TEST(
        "mysql" % "mysql-connector-java" % mysqlConnectorVersion,
        "junit" % "junit" % "4.12",
        "org.testcontainers" % "selenium" % testcontainersVersion,
        "com.datastax.oss" % "java-driver-core" % cassandraDriverVersion,
        "org.postgresql" % "postgresql" % postgresqlDriverVersion,
        "org.apache.kafka" % "kafka-clients" % kafkaDriverVersion,
        "org.mockito" % "mockito-core" % mockitoVersion,
        "io.rest-assured" % "scala-support" % restAssuredVersion
      ),

    /**
      * Publishing
      */
    useGpg := true,
    publishTo := sonatypePublishTo.value,
    publishMavenStyle := true,
    sonatypeProfileName := "testcontainers-scala",
    sonatypeProjectHosting := Some(GitLabHosting("testcontainers", "testcontainers-scala", "dimafeng@gmail.com")),
    licenses := Seq("The MIT License (MIT)" -> new URL("https://opensource.org/licenses/MIT")),

    releaseCrossBuild := true,
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
