package com.dimafeng.testcontainers

import com.dimafeng.testcontainers.scalatest.TestContainersForAll
import org.scalatest.flatspec.AnyFlatSpec
import redis.clients.jedis.Jedis

class RedisSpec extends AnyFlatSpec with TestContainersForAll {
  override type Containers = RedisContainer
  override def startContainers(): RedisContainer = RedisContainer.Def().start()

  "Redis container" should "be started" in withContainers { container =>
    val jedisClient = new Jedis(container.redisUri)

    jedisClient.connect()

    assert(jedisClient.isConnected)

    jedisClient.close()
  }
}
