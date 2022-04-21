package com.dimafeng.testcontainers

import com.google.cloud.pubsub.v1.{AckReplyConsumer, MessageReceiver}
import com.google.protobuf.ByteString
import com.google.pubsub.v1._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

import java.util.concurrent.TimeUnit
import scala.collection.JavaConverters._
import scala.concurrent.TimeoutException

class PubSubEmulatorContainerSpec extends AnyWordSpecLike with Matchers with ForAllTestContainer {

  override val container: PubSubEmulatorContainer = PubSubEmulatorContainer()

  override def beforeStop(): Unit = {
    container.topicAdminClient.close()
    container.subscriptionAdminClient.close()
    container.close()
    super.beforeStop()
  }

  "PubSub emulator container" should {

    val project = "my-project"
    val topic = "my-topic"
    val subscription = "my-subscription"
    val projectName = ProjectName.of(project)
    val topicName = TopicName.of(project, topic)
    val subscriptionName = SubscriptionName.of(project, subscription)
    val projectSubscriptionName = ProjectSubscriptionName.of(project, subscription)

    lazy val topicAdminClient = container.topicAdminClient
    lazy val subscriptionAdminClient = container.subscriptionAdminClient

    "create topic" in {
      topicAdminClient.createTopic(topicName)

      val topics = topicAdminClient.listTopics(projectName).iterateAll().asScala
      topics.size shouldEqual 1
      topics.head.getName shouldEqual s"projects/$project/topics/$topic"
    }

    "create subscription" in {
      val topics = topicAdminClient.listTopics(projectName).iterateAll().asScala.toList
      if(!topics.map(_.getName).contains(s"projects/$project/topics/$topic")) {
        topicAdminClient.createTopic(topicName)
      }

      val ackDeadlineSeconds = 10
      subscriptionAdminClient.createSubscription(
        subscriptionName,
        topicName,
        PushConfig.getDefaultInstance,
        ackDeadlineSeconds
      )

      val subscriptions = subscriptionAdminClient.listSubscriptions(projectName).iterateAll().asScala
      subscriptions.size shouldEqual 1
      subscriptions.head.getName shouldEqual s"projects/$project/subscriptions/$subscription"
    }

    "publish and consume message" in {

      val topics = topicAdminClient.listTopics(projectName).iterateAll().asScala.toList
      if(!topics.map(_.getName).contains(s"projects/$project/topics/$topic")) {
        topicAdminClient.createTopic(topicName)
      }

      val subscriptions = subscriptionAdminClient.listSubscriptions(projectName).iterateAll().asScala.toList
      if(!subscriptions.map(_.getName).contains(s"projects/$project/subscriptions/$subscription")) {
        val ackDeadlineSeconds = 10
        subscriptionAdminClient.createSubscription(
          subscriptionName,
          topicName,
          PushConfig.getDefaultInstance,
          ackDeadlineSeconds
        )
      }

      val messageText = "test message"
      val message = PubsubMessage.newBuilder().setData(ByteString.copyFromUtf8(messageText)).build()
      container.publisher(topicName).publish(message)

      val messageReceiver = new MessageReceiver() {
        override def receiveMessage(message: PubsubMessage, consumer: AckReplyConsumer): Unit = {
          message.getMessageIdBytes.toStringUtf8 shouldEqual "1"
          message.getData.toStringUtf8 shouldEqual messageText
          consumer.ack()
        }
      }
      val subscriber = container.subscriber(projectSubscriptionName, messageReceiver)
      try {
        subscriber.startAsync().awaitRunning(3L, TimeUnit.SECONDS)
        subscriber.awaitTerminated(3L, TimeUnit.SECONDS)
      } catch {
          case _: TimeoutException =>
            subscriber.stopAsync()
        }
    }

  }

}
