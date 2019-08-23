package com.utils

import org.apache.commons.pool2.impl.GenericObjectPoolConfig
import redis.clients.jedis.JedisPool


object JedisPools {

  private val config = new GenericObjectPoolConfig()
  private val pool = new JedisPool(config,"192.168.63.101",6379)
  def getResource()=pool.getResource

}
