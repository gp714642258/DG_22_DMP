package com.Rpt

import com.utils.RtpUtils
import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkConf, SparkContext}

/**
  * 网络类指标
  */
object networkmannernameRpt {
  def main(args: Array[String]): Unit = {
    if(args.length != 2){
      println("输人路径xxx")
      sys.exit()
    }
    val Array(inputPath,outputPath) = args
    val conf = new SparkConf().setAppName(this.getClass.getName)
      .setMaster("local[2]")
      .set("spark.serializer","org.apache.spark.serializer.KryoSerializer")

    val sc = new SparkContext(conf)
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


      val networkmannername = row.getAs[String]("networkmannername")


      (networkmannername,  RtpUtils.request(requestmode, processnode)
        ++ RtpUtils.click(requestmode, iseffective)
        ++ RtpUtils.Ad(iseffective, isbilling, isbid, iswin, adorderid, WinPrice, adpayment))

    }).reduceByKey((x,y)=>(x.zip(y).map(x=>x._1+x._2))).saveAsTextFile(outputPath)

    sc.stop()








  }
}
