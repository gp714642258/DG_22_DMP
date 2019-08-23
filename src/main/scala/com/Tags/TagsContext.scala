package com.Tags

import com.utils.{JedisPools, TagUtils}
import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkConf, SparkContext}

/**
  * 上下文标签
  */
object TagsContext {
  def main(args: Array[String]): Unit = {
    if(args.length != 2){
      println("目录xxxxxxxxxxxxx")
      sys.exit()
    }
    val Array(inputPath,outputPath)=args
    //创建上下文
    val conf = new SparkConf().setAppName(this.getClass.getName)
      .setMaster("local[2]")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)


    val app = sc.textFile("E:/课堂/04项目/Spark用户画像分析/app_dict.txt")
      .map(_.split("\t")).filter(_.length >= 5).map(line => {

      (line(4),line(1))
    }).collect().toMap
    val broadcast = sc.broadcast(app)


    //写入redis
    val unit: Unit = sc.textFile("E:/课堂/04项目/Spark用户画像分析/app_dict.txt")
      .map(_.split("\t")).filter(_.length >= 5).map(line => {
      (line(4), line(1))
    }).foreachPartition(itr => {
      val jedis = JedisPools.getResource()
      itr.foreach(t => {
        jedis.set(t._1, t._2)
      })
      jedis.close()
    })

/*    val df = sqlContext.read.parquet(inputPath)
    df.filter(TagUtils.OneUserId)
      .map(row => {

        val userId = TagUtils.getOneUserId(row)

        val appList = TagAppRedis.makeTags(row)

        (userId,appList.toMap)

      }).saveAsTextFile(outputPath)*/


    //获取停用词库
    val stop = sc.textFile("E:\\课堂\\04项目\\Spark用户画像分析\\stopwords.txt")
      .map((_,0)).collectAsMap()
    val bcstop = sc.broadcast(stop)




    val df = sqlContext.read.parquet(inputPath)

    //读取数据
/*
    //过滤符合id的数据
    df.filter(TagUtils.OneUserId)
      .map(row => {
       val keyList = TagKeyWord.makeTags(row,bcstop)
        (keyList.toMap)
      }).saveAsTextFile(outputPath)*/


    //1)广告位类型
  /*    df.filter(TagUtils.OneUserId)
      //接下来所有的标签都在内部实现
      .map(row => {
        //取出用户id
        val userId = TagUtils.getOneUserId(row)
      //接下来通过row数据 打上所有标签（按照需求）
        val adList = TagsAd.makeTags(row)

      (userId,adList.toMap)
      }).saveAsTextFile(outputPath)*/

//App 名称
/*    df.filter(TagUtils.OneUserId)
        .map(row => {
          val userId = TagUtils.getOneUserId(row)

          val appList = TagApp.makeTags(row,broadcast)

          (userId,appList.toMap)

        }).saveAsTextFile(outputPath)*/

    //渠道
  /*  df.filter(TagUtils.OneUserId)
        .map(row => {
          val userId = TagUtils.getOneUserId(row)
          val quList = TagQu.makeTags(row)
          (userId,quList.toMap)
        }).saveAsTextFile(outputPath)*/

    //操作系统
    /*df.filter(TagUtils.OneUserId)
      .map(row => {
        val deList = TagDevice.makeTags(row)
        (deList.toMap)
      }).saveAsTextFile(outputPath)*/

    //联网方式
/*    df.filter(TagUtils.OneUserId)
      .map(row => {
        val netList = TagNetwork.makeTags(row)
        (netList.toMap)
      }).saveAsTextFile(outputPath)*/

    //运营商
/*    df.filter(TagUtils.OneUserId)
      .map(row => {
        val ipList = TagIspname.makeTags(row)
        (ipList.toMap)
      }).saveAsTextFile(outputPath)*/

    //地域
    df.filter(TagUtils.OneUserId)
        .map(row =>{
          val locaList = TagLocation.makeTags(row)
          locaList.toMap
        }).saveAsTextFile(outputPath)


    sc.stop()


  }

}
