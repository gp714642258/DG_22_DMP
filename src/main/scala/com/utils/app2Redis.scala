package com.utils

import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkConf, SparkContext}
import redis.clients.jedis.Jedis

object app2Redis {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName(this.getClass.getName)
      .setMaster("local[2]")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)

    //写入redis
    sc.textFile("E:/课堂/04项目/Spark用户画像分析/app_dict.txt")
      .map(_.split("\t")).filter(_.length >= 5).map(line => {
      (line(4), line(1))
    }).foreachPartition(itr => {
      val jedis = new Jedis("192.168.63.101",6379)
      itr.foreach(t => {
        jedis.set(t._1, t._2)
      })
      jedis.close()
    })


    sc.stop()
  }

}
