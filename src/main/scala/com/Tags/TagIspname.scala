package com.Tags

import com.utils.Tag
import org.apache.spark.sql.Row

object TagIspname extends Tag{
  /**
    * 打标签的统一接口
    */
  override def makeTags(args: Any*): List[(String, Int)] = {
    var list = List[(String,Int)]()
    //解析参数
    val row = args(0).asInstanceOf[Row]

    val ispname = row.getAs[String]("ispname")
    list :+= (ispname,1)
    list

  }
}
