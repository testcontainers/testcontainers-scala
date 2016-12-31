import java.util.Properties

organization := "com.dimafeng"
name := "testcontainers-scala"
version := "0.4.0"

crossScalaVersions := Seq("2.11.8", "2.12.1")
scalaVersion := "2.11.8"

val testcontainersVersion = "1.1.7"
val seleniumVersion = "2.53.0"
val slf4jVersion = "1.7.21"
val scalaTestVersion = "3.0.1"
val mysqlConnectorVersion = "6.0.5"
val mockitoVersion = "1.10.19"

libraryDependencies ++= Seq(
  "org.testcontainers" % "testcontainers" % testcontainersVersion,
  "org.seleniumhq.selenium" % "selenium-java" % seleniumVersion % "provided",
  "org.testcontainers" % "selenium" % testcontainersVersion % "provided",
  "org.slf4j" % "slf4j-simple" % slf4jVersion % "provided",
  "org.scalatest" %% "scalatest" % scalaTestVersion % "provided",
  "org.testcontainers" % "mysql" % testcontainersVersion % "provided",
  "mysql" % "mysql-connector-java" % mysqlConnectorVersion % "provided",

  "junit" % "junit" % "4.12" % "test",
  "org.testcontainers" % "selenium" % testcontainersVersion % "test",
  "org.mockito" % "mockito-all" % mockitoVersion % "test"
)

val appProperties = settingKey[Properties]("Settings")

appProperties := {
  val prop = new Properties()
  IO.load(prop, Path.userHome / ".ivy2" / "settings.properties")
  prop
}

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra := (
    <url>https://github.com/dimafeng/testcontainers-scala</url>
    <scm>
      <url>git@github.com:dimafeng/testcontainers-scala.git</url>
      <connection>scm:git:git@github.com:dimafeng/testcontainers-scala.git</connection>
      <developerConnection>scm:git:git@github.com:dimafeng/testcontainers-scala.git</developerConnection>
    </scm>
    <licenses>
      <license>
        <name>The MIT License (MIT)</name>
        <url>https://opensource.org/licenses/MIT</url>
      </license>
    </licenses>
    <developers>
      <developer>
        <name>Dmitry Fedosov</name>
        <email>dimafeng@gmail.com</email>
      </developer>
    </developers>
  )

credentials += Credentials("Sonatype Nexus Repository Manager",
  "oss.sonatype.org",
  appProperties.value.getProperty("ossrhUsername"),
  appProperties.value.getProperty("ossrhPassword"))

pgpSecretRing := Path.userHome / ".gnupg" / "secring.gpg"
pgpPassphrase := Some(appProperties.value.getProperty("signing.password").toCharArray)

//val applyPgpKeyHex = TaskKey[Unit]("applyPgpKeyHex")
//applyPgpKeyHex := {
//  usePgpKeyHex(appProperties.value.getProperty("signing.password"))
//}