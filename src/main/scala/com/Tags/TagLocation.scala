package com.Tags

import com.utils.Tag
import org.apache.commons.lang3.StringUtils
import org.apache.spark.sql.Row

/**
  * 地域标签
  */
object TagLocation extends Tag{
  /**
    * 打标签的统一接口
    */
  override def makeTags(args: Any*): List[(String, Int)] = {
    var list = List[(String,Int)]()

    //解析参数
    val row = args(0).asInstanceOf[Row]


    val pro = row.getAs[String]("provincename")
    val city = row.getAs[String]("cityname")

    if(StringUtils.isNoneBlank(pro)){
      list :+= ("ZP"+pro,1)
    }
    if(StringUtils.isNoneBlank(city)){
      list :+= ("ZC"+city,1)
    }


    list

  }
}
