package com.Tags

import com.utils.{JedisPools, Tag}
import org.apache.commons.lang3.StringUtils
import org.apache.spark.sql.Row
import redis.clients.jedis.Jedis

object TagAppRedis extends Tag{
  override def makeTags(args: Any*): List[(String, Int)] = {
    var list = List[(String,Int)]()
    val jedis: Jedis = new Jedis("192.168.63.101",6379)
    //val jedis = JedisPools.getResource()
    //解析数据
    var row = args(0).asInstanceOf[Row]
    var appname = row.getAs[String]("appname")
    val appid: String = row.getAs[String]("appid")
    if(!StringUtils.isNoneBlank(appname)){
      appname = jedis.get(appid)
    }
    list:+=("APP"+appname,1)

    list
  }
}

