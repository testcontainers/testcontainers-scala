package com.dimafeng.testcontainers

import org.testcontainers.containers.{RabbitMQContainer => JavaRabbitMQContainer}
import org.testcontainers.utility.{DockerImageName, MountableFile}

case class RabbitMQContainer(
  dockerImageName: DockerImageName = DockerImageName.parse(RabbitMQContainer.defaultDockerImageName),
  adminPassword: String = RabbitMQContainer.defaultAdminPassword,
  queues: Seq[RabbitMQContainer.Queue] = Seq.empty,
  exchanges: Seq[RabbitMQContainer.Exchange] = Seq.empty,
  bindings: Seq[RabbitMQContainer.Binding] = Seq.empty,
  users: Seq[RabbitMQContainer.User] = Seq.empty,
  vhosts: Seq[RabbitMQContainer.VHost] = Seq.empty,
  vhostsLimits: Seq[RabbitMQContainer.VHostLimit] = Seq.empty,
  operatorPolicies: Seq[RabbitMQContainer.OperatorPolicy] = Seq.empty,
  policies: Seq[RabbitMQContainer.Policy] = Seq.empty,
  parameters: Seq[RabbitMQContainer.Parameter] = Seq.empty,
  permissions: Seq[RabbitMQContainer.Permission] = Seq.empty,
  pluginsEnabled: Seq[String] = Seq.empty,
  ssl: Option[RabbitMQContainer.SslConfig] = None,
  rabbitMqConfig: Option[MountableFile] = None,
  rabbitMqConfigErlang: Option[MountableFile] = None,
  rabbitMqConfigSysctl: Option[MountableFile] = None
) extends SingleContainer[JavaRabbitMQContainer] {

  import scala.collection.JavaConverters._

  override val container: JavaRabbitMQContainer = {
    val c = new JavaRabbitMQContainer(dockerImageName)

    c.withAdminPassword(adminPassword)

    vhosts.foreach {
      case RabbitMQContainer.VHost(name, Some(tracing)) => c.withVhost(name, tracing)
      case RabbitMQContainer.VHost(name, None) => c.withVhost(name)
    }

    vhostsLimits.foreach { x =>
      c.withVhostLimit(x.vhost, x.name, x.value)
    }

    queues.foreach { x =>
      c.withQueue(x.name, x.autoDelete, x.durable, toJavaArguments(x.arguments))
    }

    exchanges.foreach { x =>
      x.vhost match {
        case Some(vhost) =>
          c.withExchange(vhost, x.name, x.exchangeType, x.autoDelete, x.internal, x.durable, toJavaArguments(x.arguments))
        case None =>
          c.withExchange(x.name, x.exchangeType, x.autoDelete, x.internal, x.durable, toJavaArguments(x.arguments))
      }
    }

    bindings.foreach { x =>
      c.withBinding(x.source, x.destination, toJavaArguments(x.arguments), x.routingKey, x.destinationType)
    }

    users.foreach { x =>
      if (x.tags.isEmpty) c.withUser(x.name, x.password) else c.withUser(x.name, x.password, x.tags.asJava)
    }

    operatorPolicies.foreach { x =>
      c.withOperatorPolicy(x.name, x.pattern, toJavaArguments(x.definition), x.priority, x.applyTo)
    }

    policies.foreach { x =>
      c.withPolicy(x.name, x.pattern, toJavaArguments(x.definition), x.priority, x.applyTo)
    }

    parameters.foreach { x =>
      c.withParameter(x.component, x.name, x.value)
    }

    permissions.foreach { x =>
      c.withPermission(x.vhost, x.user, x.configure, x.write, x.read)
    }

    if (pluginsEnabled.nonEmpty) c.withPluginsEnabled(pluginsEnabled: _*)

    ssl.foreach { x =>
      c.withSSL(x.keyFile, x.certFile, x.caFile, x.verify)
      x.failIfNoCert.foreach { xx =>
        c.withEnv("RABBITMQ_SSL_FAIL_IF_NO_PEER_CERT", String.valueOf(xx))
      }
      x.verificationDepth foreach { xx =>
        c.withEnv("RABBITMQ_SSL_DEPTH", String.valueOf(xx))
      }
    }

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

  type SslVerification = JavaRabbitMQContainer.SslVerification

  case class Queue(
    name: String,
    autoDelete: Boolean = false,
    durable: Boolean = true,
    arguments: Map[String, String] = Map.empty
  )

  case class Exchange(
    name: String,
    exchangeType: String,
    autoDelete: Boolean = false,
    internal: Boolean = false,
    durable: Boolean = true,
    arguments: Map[String, String] = Map.empty,
    vhost: Option[String] = None
  )

  case class Binding(
    source: String,
    destination: String,
    routingKey: String = "",
    destinationType: String = "queue",
    arguments: Map[String, String] = Map.empty
  )

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

  case class User(
    name: String,
    password: String,
    tags: Set[String] = Set.empty
  )

  case class VHost(
    name: String,
    tracing: Option[Boolean] = None
  )

  case class VHostLimit(
    vhost: String,
    name: String,
    value: Int
  )

  case class SslConfig(
    keyFile: MountableFile,
    certFile: MountableFile,
    caFile: MountableFile,
    verify: RabbitMQContainer.SslVerification,
    failIfNoCert: Option[Boolean] = None,
    verificationDepth: Option[Int] = None
  )

  case class Def(
    dockerImageName: DockerImageName = DockerImageName.parse(RabbitMQContainer.defaultDockerImageName),
    adminPassword: String = RabbitMQContainer.defaultAdminPassword,
    queues: Seq[RabbitMQContainer.Queue] = Seq.empty,
    exchanges: Seq[RabbitMQContainer.Exchange] = Seq.empty,
    bindings: Seq[RabbitMQContainer.Binding] = Seq.empty,
    users: Seq[RabbitMQContainer.User] = Seq.empty,
    vhosts: Seq[RabbitMQContainer.VHost] = Seq.empty,
    vhostsLimits: Seq[RabbitMQContainer.VHostLimit] = Seq.empty,
    operatorPolicies: Seq[RabbitMQContainer.OperatorPolicy] = Seq.empty,
    policies: Seq[RabbitMQContainer.Policy] = Seq.empty,
    parameters: Seq[RabbitMQContainer.Parameter] = Seq.empty,
    permissions: Seq[RabbitMQContainer.Permission] = Seq.empty,
    pluginsEnabled: Seq[String] = Seq.empty,
    ssl: Option[RabbitMQContainer.SslConfig] = None,
    rabbitMqConfig: Option[MountableFile] = None,
    rabbitMqConfigErlang: Option[MountableFile] = None,
    rabbitMqConfigSysctl: Option[MountableFile] = None
  ) extends ContainerDef {

    override type Container = RabbitMQContainer

    override def createContainer(): RabbitMQContainer = {
      new RabbitMQContainer(
        dockerImageName,
        adminPassword,
        queues,
        exchanges,
        bindings,
        users,
        vhosts,
        vhostsLimits,
        operatorPolicies,
        policies,
        parameters,
        permissions,
        pluginsEnabled,
        ssl,
        rabbitMqConfig,
        rabbitMqConfigErlang,
        rabbitMqConfigSysctl
      )
    }
  }

}
