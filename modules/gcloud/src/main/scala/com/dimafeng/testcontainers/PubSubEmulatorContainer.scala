package com.dimafeng.testcontainers

import com.google.api.gax.core.{CredentialsProvider, NoCredentialsProvider}
import com.google.api.gax.grpc.GrpcTransportChannel
import com.google.api.gax.rpc.FixedTransportChannelProvider
import com.google.cloud.pubsub.v1._
import com.google.pubsub.v1.{ProjectSubscriptionName, TopicName}
import io.grpc.ManagedChannelBuilder
import org.testcontainers.containers.{PubSubEmulatorContainer => JavaPubSubEmulatorContainer}
import org.testcontainers.utility.DockerImageName

class PubSubEmulatorContainer(
  pubSubEmulatorImageName: Option[DockerImageName] = None
) extends SingleContainer[JavaPubSubEmulatorContainer] {

  override val container: JavaPubSubEmulatorContainer =
    pubSubEmulatorImageName
      .map(new JavaPubSubEmulatorContainer(_))
      .getOrElse(new JavaPubSubEmulatorContainer(PubSubEmulatorContainer.defaultImageName))

  private lazy val channelProvider: FixedTransportChannelProvider = {
    val channelBuilder = ManagedChannelBuilder.forTarget(emulatorEndpoint)
    channelBuilder.usePlaintext()
    FixedTransportChannelProvider.create(GrpcTransportChannel.create(channelBuilder.build()))
  }

  private lazy val credentialsProvider: CredentialsProvider = NoCredentialsProvider.create()

  lazy val topicAdminClient: TopicAdminClient = {
    val topicAdminSettings = TopicAdminSettings.newBuilder()
      .setTransportChannelProvider(channelProvider)
      .setCredentialsProvider(credentialsProvider)
      .build()
    TopicAdminClient.create(topicAdminSettings)
  }

  lazy val subscriptionAdminClient: SubscriptionAdminClient = {
    val subscriptionAdminSettings = SubscriptionAdminSettings.newBuilder()
      .setTransportChannelProvider(channelProvider)
      .setCredentialsProvider(credentialsProvider)
      .build()
    SubscriptionAdminClient.create(subscriptionAdminSettings)
  }

  def publisher(topicName: TopicName): Publisher = {
    Publisher.newBuilder(topicName)
      .setChannelProvider(channelProvider)
      .setCredentialsProvider(credentialsProvider)
      .build()
  }

  def subscriber(subscriptionName: ProjectSubscriptionName, receiver: MessageReceiver): Subscriber = {
    Subscriber.newBuilder(subscriptionName, receiver)
      .setChannelProvider(channelProvider)
      .setCredentialsProvider(credentialsProvider)
      .build()
  }

  def emulatorEndpoint: String = container.getEmulatorEndpoint

  override def close(): Unit = {
    topicAdminClient.close()
    subscriptionAdminClient.close()
    super.close()
  }
}

object PubSubEmulatorContainer {

  val defaultImageName: DockerImageName =
    DockerImageName.parse("gcr.io/google.com/cloudsdktool/cloud-sdk")

  def apply(
             pubsubEmulatorImageName: DockerImageName = null
           ): PubSubEmulatorContainer =
    new PubSubEmulatorContainer(Option(pubsubEmulatorImageName))

}


