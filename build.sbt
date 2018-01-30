import Dependencies._

val testcontainersVersion = "1.5.1"
val seleniumVersion = "2.53.0"
val slf4jVersion = "1.7.21"
val scalaTestVersion = "3.0.1"
val mysqlConnectorVersion = "5.1.39"
val postgresqlDriverVersion = "9.4.1212"
val mockitoVersion = "1.10.19"
val shapelessVersion = "2.3.3"

lazy val root = (project in file("."))
  .settings(
    organization in ThisBuild := "com.dimafeng",
    scalaVersion in ThisBuild := "2.12.2",
    crossScalaVersions := Seq("2.11.11", "2.12.2"),
    name := "testcontainers-scala",
    releaseCrossBuild := true,
    /**
      * Dependencies
      */
    libraryDependencies ++=
      COMPILE(
        "org.testcontainers" % "testcontainers" % testcontainersVersion,
        "com.chuusai" %% "shapeless" % shapelessVersion
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
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    scmInfo := Some(ScmInfo(url("https://github.com/testcontainers/testcontainers-scala"), "git@github.com:testcontainers/testcontainers-scala.git")),
    developers := List(
      Developer("dimafeng", "Dmitry Fedosov", "dimafeng@gmail.com", url("https://github.com/dimafeng"))
    ),
    description := "Docker containers for testing in scala ",
    licenses := Seq("The MIT License (MIT)" -> new URL("https://opensource.org/licenses/MIT"))
  )