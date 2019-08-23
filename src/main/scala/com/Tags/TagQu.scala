package com.Tags

import com.utils.Tag
import org.apache.commons.lang3.StringUtils
import org.apache.spark.sql.Row


object TagQu extends Tag{
  /**
    * 打标签的统一接口
    */
  override def makeTags(args: Any*): List[(String, Int)] = {
    var list = List[(String,Int)]()

    //解析参数
    val row = args(0).asInstanceOf[Row]

    val adplatformprovider = row.getAs[Int]("adplatformproviderid")
    if(StringUtils.isNoneBlank("adplatformproviderid")){
      list :+= ("CN"+adplatformprovider,1)
    }
    list
  }
}
