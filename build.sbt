import Dependencies._
import xerial.sbt.Sonatype._
import ReleaseTransformations._

val testcontainersVersion = "1.10.6"
val seleniumVersion = "2.53.1"
val slf4jVersion = "1.7.25"
val scalaTestVersion = "3.0.5"
val mysqlConnectorVersion = "5.1.42"
val postgresqlDriverVersion = "9.4.1212"
val mockitoVersion = "2.23.4"

lazy val compileScalastyle = taskKey[Unit]("compileScalastyle")

lazy val root = (project in file("."))
  .settings(
    organization in ThisBuild := "com.dimafeng",
    scalaVersion in ThisBuild := "2.12.8",
    crossScalaVersions := Seq("2.11.11", "2.12.8"),
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
        "org.mockito" % "mockito-core" % mockitoVersion
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

lazy val experimental = (project in file("experimental"))
  .settings(
    organization in ThisBuild := "org.testcontainers",
    scalaVersion in ThisBuild := "2.12.8",
    name := "testcontainers-scala-experimental",
    libraryDependencies ++=
      COMPILE(
        "org.testcontainers" % "testcontainers" % testcontainersVersion,
        "com.chuusai" %% "shapeless" % "2.3.3"
      ) ++ PROVIDED(
        "org.scalatest" %% "scalatest" % scalaTestVersion,
        "org.testcontainers" % "postgresql" % testcontainersVersion,
        "org.testcontainers" % "mysql" % testcontainersVersion,
      ) ++ TEST(
        "org.slf4j" % "slf4j-simple" % slf4jVersion,
      ),
  )
