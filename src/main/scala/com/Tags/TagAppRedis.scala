package com.Tags

import com.utils.{JedisPools, Tag}
import org.apache.commons.lang3.StringUtils
import org.apache.spark.sql.Row

object TagAppRedis extends Tag{
  /**
    * 打标签的统一接口
    */
  override def makeTags(args: Any*): List[(String, Int)] = {
    var list = List[(String, Int)]()
    val jedis = JedisPools.getResource()
    val row = args(0).asInstanceOf[Row]
    var appname = row.getAs[String]("appname")
    if (!StringUtils.isNotBlank(appname)) {
      val appId = row.getAs[String]("appid")
      appname = jedis.get(appId)

      list :+=("APP"+appname,1)
    }
    list
  }
}
