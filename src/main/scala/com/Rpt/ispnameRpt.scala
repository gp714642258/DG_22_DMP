package com.Rpt

import com.utils.RtpUtils
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SQLContext

/**
  * 运营商分析指标
  */
object ispnameRpt {
  def main(args: Array[String]): Unit = {
    if(args.length != 2){
      println("目录参数不正确，退出程序")
      sys.exit()
    }
    // 创建一个集合保存输入和输出目录
    val Array(inputPath,outputPath) = args
    val conf = new SparkConf().setAppName(this.getClass.getName).setMaster("local[*]")
      // 设置序列化方式 采用Kyro序列化方式，比默认序列化方式性能高
      .set("spark.serializer","org.apache.spark.serializer.KryoSerializer")

    // 创建执行入口
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)

    //获取数据
    val df = sqlContext.read.parquet(inputPath)

    val s = df.map(row => {
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

      //key
      val ispname = row.getAs[String]("ispname")

      //创建三个对应的方法处理九个指标
      (ispname, RtpUtils.request(requestmode, processnode)
        ++ RtpUtils.click(requestmode, iseffective)
        ++ RtpUtils.Ad(iseffective, isbilling, isbid, iswin, adorderid, WinPrice, adpayment))
    })

    s.reduceByKey((x,y)=>(x.zip(y).map(x=>x._1+x._2))).map(x=>x._1+","+x._2.mkString(","))
      .saveAsTextFile(outputPath)

    sc.stop()
  }
}
