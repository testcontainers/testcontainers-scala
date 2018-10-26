import Dependencies._
import xerial.sbt.Sonatype._
import ReleaseTransformations._

val testcontainersVersion = "1.9.1"
val seleniumVersion = "2.53.0"
val slf4jVersion = "1.7.21"
val scalaTestVersion = "3.0.1"
val mysqlConnectorVersion = "5.1.39"
val postgresqlDriverVersion = "9.4.1212"
val mockitoVersion = "1.10.19"

lazy val compileScalastyle = taskKey[Unit]("compileScalastyle")

lazy val root = (project in file("."))
  .settings(
    organization in ThisBuild := "com.dimafeng",
    scalaVersion in ThisBuild := "2.12.2",
    crossScalaVersions := Seq("2.11.11", "2.12.2"),
    name := "testcontainers-scala",
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
        "org.testcontainers" % "mysql" % testcontainersVersion,
        "org.testcontainers" % "postgresql" % testcontainersVersion
      )
        ++ TEST(
        "mysql" % "mysql-connector-java" % mysqlConnectorVersion,
        "junit" % "junit" % "4.12",
        "org.testcontainers" % "selenium" % testcontainersVersion,
        "org.postgresql" % "postgresql" % postgresqlDriverVersion,
        "org.mockito" % "mockito-all" % mockitoVersion
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
      runTest,
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
