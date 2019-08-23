package com.Tags

import com.utils.Tag
import org.apache.spark.sql.Row

/**
  * 设备
  */
object TagNetwork extends Tag{
  /**
    * 打标签的统一接口
    */
  override def makeTags(args: Any*): List[(String, Int)] = {
    var list = List[(String,Int)]()

    //解析参数
    val row = args(0).asInstanceOf[Row]

    val networkmannername = row.getAs[String]("networkmannername")

    list :+= (networkmannername + "D0002000"+row.getAs[Int]("networkmannerid"),1)

    list
  }
}
