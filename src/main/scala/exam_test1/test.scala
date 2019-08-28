package exam_test1


import com.Tags.TagAppRedis
import org.apache.spark
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkConf, SparkContext}


object test {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName(this.getClass.getName)
      .setMaster("local[2]")

    val sc = new SparkContext(conf)

    val sqlContext = new SQLContext(sc)

    val rddStr: RDD[String] = sc.textFile("C:\\Users\\Administrator\\Desktop\\json.txt")

   /* val df = sqlContext.read.json("C:\\Users\\Administrator\\Desktop\\json.txt")

    df.registerTempTable("aaa")
    sqlContext.sql("select * from aaa ").show()*/

    val bs = rddStr.map(str => {
      val arr: Array[String] = AmapUtil.getBusinessFromAmap(str).split(",")
      (arr(0),arr(arr.length-1))
    })

    bs.foreach(println)

    sc.stop()
  }
}
