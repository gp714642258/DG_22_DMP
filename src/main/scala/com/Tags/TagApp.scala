package com.Tags
import com.utils.Tag
import org.apache.commons.lang3.StringUtils
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.sql.Row

/**
  * App名称标签
  */
object TagApp extends Tag{
  /**
    * 打标签的统一接口
    */
  override def makeTags(args: Any*): List[(String, Int)] = {
    var list = List[(String,Int)]()
    //解析参数
    val row = args(0).asInstanceOf[Row]
    val broadcast = args(1).asInstanceOf[Broadcast[Map[String, String]]]







    val appId = row.getAs[String]("appid")
    val appName = if(row.getAs[String]("appname").isEmpty()) broadcast.value.get("appid") else row.getAs[String]("appname")



    list :+=("APP"+appName,1)


    list

  }
}
