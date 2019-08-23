package com.Rpt

import com.utils.RtpUtils
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SQLContext

/**
  * 地域分布指标
  */
object LocationRpt {
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
/*    df.registerTempTable("location")
    sqlContext.sql("select provincename,cityname , " +
      "sum(case when requestmode=1 and processnode>=1 then 1 else 0 end) t1 ," +
      " sum(case when requestmode=1 and processnode>=2 then 1 else 0 end) t2 , " +
      "sum(case when requestmode=1 and processnode=3 then 1 else 0 end ) t3 , " +
      "sum(case when iseffective=1 and isbilling=1 and isbid=1 then 1 else 0 end) t4 , " +
      "sum(case when iseffective=1 and isbilling=1 and iswin=1 and adorderid !=1 then 1 else 0 end) t5 , " +
      "sum(case when requestmode=2 and iseffective=1 then 1 else 0 end) t6 , " +
      "sum(case when requestmode=3 and iseffective=1 then 1 else 0 end) t7 , " +
      "sum(case when iseffective=1 and isbilling=1 and iswin=1 then 1 else 0 end) t8 , " +
      "sum(case when iseffective=1 and isbilling=1 and iswin=1 then 1 else 0 end) t9 " +
      "from location " +
      "group by provincename,cityname ").show()*/










    //将数据进行处理，统计各个指标
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

      //key值 是地域的省市
      val pro = row.getAs[String]("provincename")
      val city = row.getAs[String]("cityname")

      //创建三个对应的方法处理九个指标
      ((pro, city), RtpUtils.request(requestmode, processnode)
        ++ RtpUtils.click(requestmode, iseffective)
        ++ RtpUtils.Ad(iseffective, isbilling, isbid, iswin, adorderid, WinPrice, adpayment))
    })

    s.reduceByKey((x,y)=>(x.zip(y).map(x=>x._1+x._2))).map(x=>x._1+","+x._2.mkString(","))
        .saveAsTextFile(outputPath)


    //如果存入mysql 需要使用foreachPartition
    //需要自己写一个连接池


    sc.stop()

  }
}
