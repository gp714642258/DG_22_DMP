package com.utils

import java.sql.{Connection, DriverManager}
import java.util

import com.typesafe.config.ConfigFactory



object JdbcConnectionPool {
  private val config = ConfigFactory.load("jdbc.properties")
  //连接池总数
  private val max_connection = config.getString("jdbc.max_connection")
  //产生的连接数
  private val connection_num = config.getString("jdbc.connection_num")
  //当前连接池已产生的连接数
  private var current_num = 0
  //创建一个连接池
  private val pools = new util.LinkedList[Connection]()
  //获取连接信息
  private val driver = config.getString("jdbc.driver")
  private val url = config.getString("jdbc.url")
  private val username = config.getString("jdbc.username")
  private val password = config.getString("jdbc.password")

  /*
     加载驱动
   */
  private def before(): Unit ={
    if(current_num > max_connection.toInt && pools.isEmpty){
      println("busyness")
      Thread.sleep(2000)
      before()
    }else{
      Class.forName(driver)
    }
  }

  /*
     创建连接
   */
  private def initConn()={
    DriverManager.getConnection(url,username,password)
  }

  /*
     初始化连接池
   */
  private def initConnectionPool()={
    AnyRef.synchronized({
      if(pools.isEmpty){
        before()
        for(i <- 1 to connection_num.toInt){
          pools.push(initConn())
          current_num += 1
        }
      }
      pools
    })
  }

  /*
     获取连接
   */
  def getConn()={
    initConnectionPool()
    pools.poll()
  }

  /*
     释放连接
   */
  def releaseCon(con:Connection): Unit ={
    pools.push(con)
  }


}
