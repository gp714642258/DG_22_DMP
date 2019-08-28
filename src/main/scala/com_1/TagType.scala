
import com.alibaba.fastjson.JSON
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SQLContext
import org.json.JSONObject

import scala.collection.mutable

object TagType {
  def main(args: Array[String]): Unit = {

    var list: List[String] = List()

    val conf = new SparkConf().setAppName(this.getClass.getName).setMaster("local[2]")
    val sc = new SparkContext(conf)
    val sQLContext = new SQLContext(sc)

    val log = sc.textFile("C:\\Users\\Administrator\\Desktop\\json.txt")
    val logs = log.collect().toBuffer
    //println(logs)

    for(i <- 0 until logs.length){
      val arr = logs(i).toString
      //解析json串
      val jsonprase = JSON.parseObject(arr)
      //判断状态是否成功
      val status = jsonprase.getIntValue("status")
      if(status == 0) return  ""

      //接下来解析内部json串
      val regeocodeJson = jsonprase.getJSONObject("regeocode")
      if(regeocodeJson == null || regeocodeJson.keySet().isEmpty ) return  ""

      val poisObject = regeocodeJson.getJSONArray("pois")
      if(poisObject == null || poisObject.isEmpty)  return  null

      // 创建集合 保存数据
      val buffer = collection.mutable.ListBuffer[String]()
      //循环输出
      for(arr <- poisObject.toArray()){
        if(arr.isInstanceOf[JSONObject]){
          val json = arr.asInstanceOf[JSONObject]
          buffer.append(json.getString("type"))
        }
      }

      list :+= buffer.mkString(";")

    }







  }
}