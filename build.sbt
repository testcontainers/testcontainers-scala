import Dependencies._

val testcontainersVersion = "1.6.0"
val seleniumVersion = "2.53.0"
val slf4jVersion = "1.7.21"
val scalaTestVersion = "3.0.1"
val mysqlConnectorVersion = "5.1.39"
val postgresqlDriverVersion = "9.4.1212"
val mockitoVersion = "1.10.19"
val shapelessVersion = "2.3.3"

lazy val compileScalastyle = taskKey[Unit]("compileScalastyle")

lazy val root = (project in file("."))
  .settings(
    organization in ThisBuild := "com.dimafeng",
    scalaVersion in ThisBuild := "2.12.2",
    crossScalaVersions := Seq("2.11.11", "2.12.2"),
    name := "testcontainers-scala",
    releaseCrossBuild := true,
    compileScalastyle := scalastyle.in(Compile).toTask("").value,
    test in Test := (test in Test).dependsOn(compileScalastyle in Compile).value,
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
    useGpg := true,
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    projectInfo := ModuleInfo(
      "testcontainers-scala",
      "Docker containers for testing in scala ",
      Some(url("https://github.com/testcontainers/testcontainers-scala")),
      Some(2016),
      Vector(("MIT", url("https://github.com/testcontainers/testcontainers-scala/blob/master/LICENSE"))),
      "dimafeng",
      None,
      Some(ScmInfo(url("https://github.com/testcontainers/testcontainers-scala"), "git@github.com:testcontainers/testcontainers-scala.git")),
      Vector(Developer("", "Dmitry Fedosov", "dimafeng@gmail.com", url("http://dimafeng.com")))
    ),
    developers := List(
      Developer("dimafeng", "Dmitry Fedosov", "dimafeng@gmail.com", url("https://github.com/dimafeng"))
    ),
    description := "Docker containers for testing in scala ",
    licenses := Seq("The MIT License (MIT)" -> new URL("https://opensource.org/licenses/MIT"))
  )