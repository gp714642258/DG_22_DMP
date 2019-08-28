package com.utils


import redis.clients.jedis.{JedisPool, JedisPoolConfig}


object JedisPools {

  private val config = new JedisPoolConfig()
  private val pool = new JedisPool(config,"192.168.63.101",6379)
  def getResource()=pool.getResource

}
