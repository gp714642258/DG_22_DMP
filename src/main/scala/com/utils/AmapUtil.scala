package com.utils

import com.alibaba.fastjson.{JSON, JSONObject}
import com.sun.deploy.net.HttpUtils

/**
  * 商圈解析工具
  */
object AmapUtil {

  //获取高德地图商圈信息
  def getBusinessFromAmap(long:Double,lat:Double):String ={
    //
    val location = long +","+lat
   // val urlStr = "https://restapi.amap.com/v3/geocode/regeo?output=xml&location="+location+"&key=0decd600d91cda84be476729ebf8d319&radius=1000"
   val urlStr = "https://restapi.amap.com/v3/geocode/regeo?key=0decd600d91cda84be476729ebf8d319&location="+location
    //调用请求
    val jsonstr = HttpUtil.get(urlStr)
   // val jsonstr = "{\"status\":\"1\",\"regeocode\":{\"addressComponent\":{\"city\":[],\"province\":\"北京市\",\"adcode\":\"110108\",\"district\":\"海淀区\",\"towncode\":\"110108015000\",\"streetNumber\":{\"number\":\"5号\",\"location\":\"116.310454,39.9927339\",\"direction\":\"东北\",\"distance\":\"94.5489\",\"street\":\"颐和园路\"},\"country\":\"中国\",\"township\":\"燕园街道\",\"businessAreas\":[{\"location\":\"116.303364,39.97641\",\"name\":\"万泉河\",\"id\":\"110108\"},{\"location\":\"116.314222,39.98249\",\"name\":\"中关村\",\"id\":\"110108\"},{\"location\":\"116.294214,39.99685\",\"name\":\"西苑\",\"id\":\"110108\"}],\"building\":{\"name\":\"北京大学\",\"type\":\"科教文化服务;学校;高等院校\"},\"neighborhood\":{\"name\":\"北京大学\",\"type\":\"科教文化服务;学校;高等院校\"},\"citycode\":\"010\"},\"formatted_address\":\"北京市海淀区燕园街道北京大学\"},\"info\":\"OK\",\"infocode\":\"10000\"}"
    //解析json串
    val jsonparse = JSON.parseObject(jsonstr)
    //判断状态是否成功
    val status = jsonparse.getIntValue("status")
    if(status ==0) return ""
    //接下啦解析内部json串，判断每个key的value都不能为空
    val regeocodeJson = jsonparse.getJSONObject("regeocode")
    if(regeocodeJson == null || regeocodeJson.keySet().isEmpty) return  ""

    val addressComponentJson = regeocodeJson.getJSONObject("addressComponent")
    if(addressComponentJson == null || addressComponentJson.keySet().isEmpty) return  ""


    val businessAreasArray = addressComponentJson.getJSONArray("businessAreas")
    if(businessAreasArray == null || businessAreasArray.isEmpty) return  ""

    //创建集合，保存数据
    val buffer = collection.mutable.ListBuffer[String]()
    //循环输出
    for(item <- businessAreasArray.toArray){
      if(item.isInstanceOf[JSONObject]){
        val json = item.asInstanceOf[JSONObject]
        buffer.append(json.getString("name"))
      }
    }
    buffer.mkString(",")
  }




}
