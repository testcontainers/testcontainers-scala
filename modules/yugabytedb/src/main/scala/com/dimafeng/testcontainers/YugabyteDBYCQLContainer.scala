package com.dimafeng.testcontainers

import org.testcontainers.containers.{YugabyteDBYCQLContainer => JavaYugabyteYCQLContainer}
import org.testcontainers.utility.DockerImageName

import java.net.InetSocketAddress

class YugabyteDBYCQLContainer(
                               underlying: JavaYugabyteYCQLContainer
                             ) extends SingleContainer[JavaYugabyteYCQLContainer] {

  override val container: JavaYugabyteYCQLContainer = underlying

  def keyspace: String = container.getKeyspace

  def localDc: String = container.getLocalDc

  def username: String = container.getUsername

  def password: String = container.getPassword

  def contactPoint: InetSocketAddress = container.getContactPoint

}

object YugabyteDBYCQLContainer {

  val defaultImage           = "yugabytedb/yugabyte"
  val defaultTag             = "2.20.7.1-b10"
  val defaultDockerImageName = s"$defaultImage:$defaultTag"

  val ycqlPort    = 9042
  val masterDashboardPort = 7000
  val tserverDashboardPort = 9000

  case class Def(
                  dockerImageName: DockerImageName = DockerImageName.parse(YugabyteDBYCQLContainer.defaultDockerImageName),
                  private val builder: List[JavaYugabyteYCQLContainer => JavaYugabyteYCQLContainer] = List.empty
                ) extends ContainerDef {
    override type Container = YugabyteDBYCQLContainer

    def withKeyspaceName(keyspace: String): Def =
      copy(builder = ((_: JavaYugabyteYCQLContainer).withKeyspaceName(keyspace)) :: builder)

    def withUsername(username: String): Def =
      copy(builder = ((_: JavaYugabyteYCQLContainer).withUsername(username)) :: builder)

    def withPassword(password: String): Def =
      copy(builder = ((_: JavaYugabyteYCQLContainer).withPassword(password)) :: builder)

    def withInitScript(script: String): Def =
      copy(builder = ((_: JavaYugabyteYCQLContainer).withInitScript(script)) :: builder)

    override def createContainer(): YugabyteDBYCQLContainer = {
      new YugabyteDBYCQLContainer(
        builder
          .foldRight(new JavaYugabyteYCQLContainer(dockerImageName))((f, underlying) => f(underlying))
      )
    }
  }
}
