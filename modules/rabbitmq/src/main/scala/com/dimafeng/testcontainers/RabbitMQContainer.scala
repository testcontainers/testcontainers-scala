package com.dimafeng.testcontainers

import org.testcontainers.rabbitmq.{RabbitMQContainer => JavaRabbitMQContainer}
import org.testcontainers.utility.{DockerImageName, MountableFile}

case class RabbitMQContainer(
  dockerImageName: DockerImageName = DockerImageName.parse(RabbitMQContainer.defaultDockerImageName),
  adminPassword: String = RabbitMQContainer.defaultAdminPassword,
  operatorPolicies: Seq[RabbitMQContainer.OperatorPolicy] = Seq.empty,
  policies: Seq[RabbitMQContainer.Policy] = Seq.empty,
  parameters: Seq[RabbitMQContainer.Parameter] = Seq.empty,
  permissions: Seq[RabbitMQContainer.Permission] = Seq.empty,
  pluginsEnabled: Seq[String] = Seq.empty,
  rabbitMqConfig: Option[MountableFile] = None,
  rabbitMqConfigErlang: Option[MountableFile] = None,
  rabbitMqConfigSysctl: Option[MountableFile] = None
) extends SingleContainer[JavaRabbitMQContainer] {

  import scala.collection.JavaConverters._

  override val container: JavaRabbitMQContainer = {
    val c = new JavaRabbitMQContainer(dockerImageName)

    c.withAdminPassword(adminPassword)

    rabbitMqConfig.foreach(c.withRabbitMQConfig)
    rabbitMqConfigErlang.foreach(c.withRabbitMQConfigErlang)
    rabbitMqConfigSysctl.foreach(c.withRabbitMQConfigSysctl)

    c
  }

  private def toJavaArguments(map: Map[String, String]): java.util.Map[String, AnyRef] = map.map { case (k, v) =>
    (k, v: AnyRef)
  }.asJava

  def adminUsername: String = container.getAdminUsername

  def amqpPort: Int = container.getAmqpPort
  def amqpsPort: Int = container.getAmqpsPort
  def httpPort: Int = container.getHttpPort
  def httpsPort: Int = container.getHttpsPort

  def amqpUrl: String = container.getAmqpUrl
  def amqpsUrl: String = container.getAmqpsUrl
  def httpUrl: String = container.getHttpUrl
  def httpsUrl: String = container.getHttpsUrl
}

object RabbitMQContainer {

  val defaultImage = "rabbitmq"
  val defaultTag = "3.7-management-alpine"
  val defaultDockerImageName = s"$defaultImage:$defaultTag"
  val defaultAdminPassword = "guest"

  case class OperatorPolicy(
    name: String,
    pattern: String,
    definition: Map[String, String],
    priority: Int = 0,
    applyTo: String = ""
  )

  case class Policy(
    name: String,
    pattern: String,
    definition: Map[String, String],
    priority: Int = 0,
    applyTo: String = ""
  )

  case class Parameter(
    component: String,
    name: String,
    value: String
  )

  case class Permission(
    vhost: String,
    user: String,
    configure: String,
    write: String,
    read: String
  )

  case class Def(
    dockerImageName: DockerImageName = DockerImageName.parse(RabbitMQContainer.defaultDockerImageName),
    adminPassword: String = RabbitMQContainer.defaultAdminPassword,
    operatorPolicies: Seq[RabbitMQContainer.OperatorPolicy] = Seq.empty,
    policies: Seq[RabbitMQContainer.Policy] = Seq.empty,
    parameters: Seq[RabbitMQContainer.Parameter] = Seq.empty,
    permissions: Seq[RabbitMQContainer.Permission] = Seq.empty,
    pluginsEnabled: Seq[String] = Seq.empty,
    rabbitMqConfig: Option[MountableFile] = None,
    rabbitMqConfigErlang: Option[MountableFile] = None,
    rabbitMqConfigSysctl: Option[MountableFile] = None
  ) extends ContainerDef {

    override type Container = RabbitMQContainer

    override def createContainer(): RabbitMQContainer = {
      new RabbitMQContainer(
        dockerImageName,
        adminPassword,
        operatorPolicies,
        policies,
        parameters,
        permissions,
        pluginsEnabled,
        rabbitMqConfig,
        rabbitMqConfigErlang,
        rabbitMqConfigSysctl
      )
    }
  }

}
