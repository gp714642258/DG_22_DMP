package com.Tags

import com.utils.Tag
import org.apache.spark.sql.Row

/**
  * 设备
  */
object TagDevice extends Tag{
  /**
    * 打标签的统一接口
    */
  override def makeTags(args: Any*): List[(String, Int)] = {
    var list = List[(String,Int)]()

   //解析参数
    val row = args(0).asInstanceOf[Row]

    val client = if (row.getAs[Int]("client")==1)"1 android " else if(row.getAs[Int]("client")==2) "2 ios " else if(row.getAs[Int]("client")==3)"3 WinPhone" else "_ 其他"


    list :+= (client + "D0001000"+row.getAs[Int]("client"),1)

    list
  }
}
