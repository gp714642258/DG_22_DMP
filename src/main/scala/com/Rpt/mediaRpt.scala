package com.Rpt

import com.utils.RtpUtils
import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkConf, SparkContext}

/**
  * 媒体分析
  */
object mediaRpt {
  def main(args: Array[String]): Unit = {
    val Array(inputPath,outputPath) = args
    if(args.length != 2){
      println("xxxxxxxxxxxxx")
      sys.exit()
    }
    val conf = new SparkConf().setAppName(this.getClass.getName).setMaster("local[2]")
      .set("spark.serializer","org.apache.spark.serializer.KryoSerializer")

    val sc = new SparkContext(conf)

    val app = sc.textFile("E:/课堂/04项目/Spark用户画像分析/app_dict.txt")
      .map(_.split("\t")).filter(_.length >= 5).map(line => {

      (line(4),line(1))
    }).collect().toMap
    val broadcast = sc.broadcast(app)
    val sqlContext = new SQLContext(sc)
    val df = sqlContext.read.parquet(inputPath)

    df.map(row => {
      //把需要的字段全部取到
      val requestmode = row.getAs[Int]("requestmode")
      val processnode = row.getAs[Int]("processnode")
      val iseffective = row.getAs[Int]("iseffective")
      val isbilling = row.getAs[Int]("isbilling")
      val isbid = row.getAs[Int]("isbid")
      val iswin = row.getAs[Int]("iswin")
      val adorderid = row.getAs[Int]("adorderid")
      val WinPrice = row.getAs[Double]("winprice")
      val adpayment = row.getAs[Double]("adpayment")


      val appid = row.getAs[String]("appid")
      val appname = if(row.getAs[String]("appname").isEmpty()) broadcast.value.get("appid") else row.getAs[String]("appname")



      (appname,  RtpUtils.request(requestmode, processnode)
        ++ RtpUtils.click(requestmode, iseffective)
        ++ RtpUtils.Ad(iseffective, isbilling, isbid, iswin, adorderid, WinPrice, adpayment))

    }).reduceByKey((x,y)=>(x.zip(y).map(x=>x._1+x._2))).saveAsTextFile(outputPath)



    sc.stop()

  }
}
