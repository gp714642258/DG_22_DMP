package exam_test1

import com.alibaba.fastjson.{JSON, JSONObject}
import com.utils.HttpUtil
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SQLContext

/**
  * 商圈解析工具
  */
object AmapUtil {


  def getBusinessFromAmap(json:String):String ={

 //
// val location = long +","+lat
 //val urlStr = "https://restapi.amap.com/v3/geocode/regeo?output=xml&location="+location+"&key=0decd600d91cda84be476729ebf8d319&radius=1000"
 //调用请求
// val jsonstr = HttpUtil.get(urlStr)

 val jsonstr =json
    //解析json串
 val jsonparse = JSON.parseObject(jsonstr)
 //判断状态是否成功
 val status = jsonparse.getIntValue("status")
 if(status ==0) return ""
 //接下啦解析内部json串，判断每个key的value都不能为空
 val regeocodeJson = jsonparse.getJSONObject("regeocode")
 if(regeocodeJson == null || regeocodeJson.keySet().isEmpty) return  ""

 val poisJson = regeocodeJson.getJSONArray("pois")
 if(poisJson == null || poisJson.isEmpty) return  ""



 //创建集合，保存数据
 val buffer = collection.mutable.ListBuffer[String]()
 for(item <- poisJson.toArray){
   if(item.isInstanceOf[JSONObject]){
     val json = item.asInstanceOf[JSONObject]
     buffer.append(json.getString("businessarea"))
   }
 }

 val st = buffer.mkString(",")+","+buffer.size

    st

   /* val buffer1 = collection.mutable.ListBuffer[String]()
    for(item <- poisJson.toArray){
      if(item.isInstanceOf[JSONObject]){
        val json = item.asInstanceOf[JSONObject]
        buffer1.append(json.getString("type"))
      }
    }

    buffer1.mkString(",")*/
}




}
