package com.utils

import com.Tags.TagBusiness
import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkConf, SparkContext}

/**
  * 测试类
  */
object test {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName(this.getClass.getName)
      .setMaster("local[2]")
    val sc = new SparkContext(conf)
    /*val list = List("116.310003,39.991957")
    val rdd = sc.makeRDD(list)
    val bs = rdd.map(t=>{
      val arr = t.split(",")
     AmapUtil.getBusinessFromAmap(arr(0).toDouble,arr(1).toDouble)
    })
    bs.foreach(println)*/

    val ssc = new SQLContext(sc)
    val df = ssc.read.parquet("E:\\xiangmu1\\good1")

    df.map(row => {
      val business = TagBusiness.makeTags(row)
      business
    }).foreach(println)

  }
}
