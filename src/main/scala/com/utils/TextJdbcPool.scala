package com.utils

object TextJdbcPool {

  def main(args: Array[String]): Unit = {
    //从连接池中获取一个连接
    val connection = JdbcConnectionPool.getConn()
    //创建一个语句对象
    val statement = connection.createStatement()
    //读取mysql数据库中 表中数据
    val rs = statement.executeQuery("select * from text5")

    //输出表中信息
    while (rs.next()){
      println(rs.getString(1),rs.getString(2),rs.getInt(3))
    }
    //释放资源
    JdbcConnectionPool.releaseCon(connection)

  }
}
