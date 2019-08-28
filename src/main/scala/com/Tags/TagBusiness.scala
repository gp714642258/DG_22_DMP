package com.Tags

import ch.hsr.geohash.GeoHash
import com.Utils2Type.Utils2Type
import com.utils.{AmapUtil, JedisPools, Tag}
import org.apache.commons.lang3.StringUtils
import org.apache.spark.sql.Row

/**
  * 商圈标签
  */
object TagBusiness extends  Tag{
  /**
    * 打标签的统一接口
    */
  override def makeTags(args: Any*): List[(String, Int)] = {
    var list = List[(String, Int)]()
    //解析参数
    val row = args(0).asInstanceOf[Row]
    //获取商圈经纬度，过滤经纬度

    val long = row.getAs[String]("long")
    val lat = row.getAs[String]("lat")
    if (Utils2Type.toDouble(row.getAs[String]("long")) >= 73.0
      && Utils2Type.toDouble(row.getAs[String]("long")) <= 135.0
      && Utils2Type.toDouble(row.getAs[String]("lat")) >= 3.0
      && Utils2Type.toDouble(row.getAs[String]("lat")) <= 54.0) {


      //先去数据库中获取商圈
      val business = getBusiness(long.toDouble, lat.toDouble)
      //判断缓存中是否有此商圈
      if (StringUtils.isNoneBlank(business)) {
        val lines = business.split(",")
        lines.foreach(f => list :+= (f, 1))
      }
    }
    list
  }
  /**
    * 获取商圈信息
    */
  def getBusiness(long: Double,lat : Double) :String = {
    //转换geohash字符串
    val geohash = GeoHash.geoHashStringWithCharacterPrecision(lat, long, 8)

    //--    val geohash = GeoHash.geoHashStringWithCharacterPrecision(lat,long,8)
    //去数据库查询xx
    var business = redis_queryBusniess(geohash)
    //判断商圈是否为空
    if (business == null || business.length == 0) {
      //通过经纬度获取商圈
      val business = AmapUtil.getBusinessFromAmap(long.toDouble, lat.toDouble)
      //如果调用高德地图解析商圈，那么需要将此次商圈存入redis
      redis_insertBusiness(geohash, business)
    }
    business
  }

    /**
      * 获取商圈信息
      */
    def redis_queryBusniess(geohash: String): String = {
      val jedis = JedisPools.getResource()
      val business = jedis.get(geohash)
      jedis.close()
      business
    }

    /**
      * 存入商圈道redis
      */
    def redis_insertBusiness(geoHash: String, business: String): Unit = {
      val jedis = JedisPools.getResource()
      jedis.set(geoHash,business)
      jedis.close()

    }

}