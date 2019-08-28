package com.Tags

import com.typesafe.config.ConfigFactory
import com.utils.TagUtils
import org.apache.hadoop.hbase.{HColumnDescriptor, HTableDescriptor, TableName}
import org.apache.hadoop.hbase.client.ConnectionFactory
import org.apache.hadoop.hbase.mapred.TableOutputFormat
import org.apache.hadoop.mapred.JobConf
import org.apache.spark.graphx.{Edge, Graph}
import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkConf, SparkContext}

/**
  * 上下文标签
  */
object TagContext3 {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName(this.getClass.getName).setMaster("local[2]")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)
    //todo 调用Hbase API
    //加载配置文件
    val load = ConfigFactory.load()
    val hbaseTableName = load.getString("hbase.TableName")
    //创建hadoop任务
    val configuration = sc.hadoopConfiguration
    configuration.set("hbase.zookeeper.quorum",load.getString("hbase.host"))
    //创建HbaseConnection
    val hbconn = ConnectionFactory.createConnection(configuration)
    val hbadmin = hbconn.getAdmin
    //判断表是否可用
    if(!hbadmin.tableExists(TableName.valueOf(hbaseTableName))){
      //创建表操作
      val tableDescriptor = new HTableDescriptor(TableName.valueOf(hbaseTableName))
      val descriptor = new HColumnDescriptor("tags")
      tableDescriptor.addFamily(descriptor)
      hbadmin.createTable(tableDescriptor)
      hbadmin.close()
      hbconn.close()
    }
    //创建jobconf
    val jobconf = new JobConf(configuration)
    //指定输出类型和表
    jobconf.setOutputFormat(classOf[TableOutputFormat])
    jobconf.set(TableOutputFormat.OUTPUT_TABLE,hbaseTableName)


    //读取数据
    val df = sqlContext.read.parquet("E://xiangmu1/good1")
    //读取字段文件
    val map = sc.textFile("E:/课堂/04项目/Spark用户画像分析/app_dict.txt")
      .map(_.split("\t",-1))
      .filter(_.length>=5).map(arr => (arr(4),arr(1))).collectAsMap()
    //将处理好的数据广播
    val broadcast =sc.broadcast(map)

    //获取停用词库
    val stopword = sc.textFile("E:\\课堂\\04项目\\Spark用户画像分析\\stopwords.txt")
    val bcstopword = sc.broadcast(stopword)
    //过滤符合ID的数据
    val baseRdd = df.filter(TagUtils.OneUserId)
      .map(row => {
        val userList = TagUtils.getAllUserId(row)
        (userList,row)
      })
    //构建点集合
   val vertiesRDD =  baseRdd.flatMap(tp=>{
      val row = tp._2
      //所有标签
      val adList = TagsAd.makeTags(row)
      val appList = TagApp.makeTags(row,broadcast)
      val keywordList = TagKeyWord.makeTags(row,bcstopword)
      val dvList = TagDevice.makeTags(row)
      val loactionList = TagLocation.makeTags(row)
      val business = TagBusiness.makeTags(row)
      val AllTag = adList++appList++keywordList++dvList++loactionList++business
      //List((String,Int))
      //保证其中一个点携带着所有标签，同时也保存所有userid
      val VD = tp._1.map((_,0))++AllTag
      //处理所有点的集合
      tp._1.map(uId=>{
        //保证一个点携带标签
        if(tp._1.head.equals(uId)){
          (uId.hashCode.toLong,VD)//uId是String 需要转换Long
        }else {
          (uId.hashCode.toLong,List.empty)
        }
      })
    })
//    vertiesRDD.take(50).foreach(println)

    //构建边的集合
    val edges = baseRdd.flatMap(tp =>{
      tp._1.map(uId => Edge(tp._1.head.hashCode,uId.hashCode,0))
    })
//    edges.take(20).foreach(println)

    //构建图
    val graph = Graph(vertiesRDD,edges)
    //取出顶点  使用的是图计算中的连通图算法
    val vertices = graph.connectedComponents().vertices
    //处理所有的标签和id
    vertices.join(vertiesRDD).map{
      case (uId,(conId,tagsAll))=>(conId,tagsAll)
    }.reduceByKey((list1,list2)=>{
      (list1 ++ list2).groupBy(_._1).mapValues(_.map(_._2).sum).toList
    }).take(20).foreach(println)


    sc.stop()



//      .map(row => {
//        val userId = TagUtils.getAllUserId(row)
//        //通过row数据 打上所有标签
//        val adList = TagsAd.makeTags(row)
//        val appList = TagApp.makeTags(row,broadcast)
//        val keywordList = TagKeyWord.makeTags(row,bcstopword)
//        val dvList = TagDevice.makeTags(row)
//        val loactionList = TagLocation.makeTags(row)
//        val business = TagBusiness.makeTags(row)
//        (userId,adList++appList++keywordList++dvList++loactionList++business)
//      })
//      .reduceByKey((list1,list2)=>
//        (list1:::list2)
//          .groupBy(_._1)
//          .mapValues(_.foldLeft[Int](0)(_+_._2))
//          .toList
//      )
  }
}
