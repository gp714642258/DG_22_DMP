package com.Rpt

import com.utils.RtpUtils
import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkConf, SparkContext}

/**
  * 设备类需求
  */
object deviceTypeRtp {
  def main(args: Array[String]): Unit = {
    val Array(inputPath,outputPath) = args
    if(args.length != 2){
      println("xxxxxxxxxxxxx")
      sys.exit()
    }
    val conf = new SparkConf().setAppName(this.getClass.getName).setMaster("local[2]")
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


      val devicetype = if (row.getAs[Int]("devicetype")==1)"手机 " else if(row.getAs[Int]("devicetype")==2) "平板 "  else "其他"



      (devicetype,  RtpUtils.request(requestmode, processnode)
        ++ RtpUtils.click(requestmode, iseffective)
        ++ RtpUtils.Ad(iseffective, isbilling, isbid, iswin, adorderid, WinPrice, adpayment))

    }).reduceByKey((x,y)=>(x.zip(y).map(x=>x._1+x._2))).saveAsTextFile(outputPath)





    sc.stop()

  }
}
