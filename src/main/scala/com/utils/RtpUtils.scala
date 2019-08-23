package com.utils

/**
  * 指标方法
  */
object RtpUtils {

  //此方法处理请求数
  def request (requestmode:Int,processnode:Int):List[Double]={
    //判断 返回一个list  三个参数
    var a =0
    var b =0
    var c =0
    if(requestmode==1){
      if(processnode >=1){
        a += 1
      }else if (processnode >=2){
        b += 1
      }else if(processnode ==3){
        c += 1
      }
    }
    List(a,b,c)
  }

  //此方法处理展示点击数
  def click(requestmode:Int,iseffective:Int):List[Double]={
    var a = 0
    var b = 0
    if(iseffective==1){
      if(requestmode==2){
        a += 1
      }else if(requestmode==3){
        b += 1
      }
    }
    List(a,b)
  }


  //此方法处理竞价操作
  def Ad(iseffective:Int,isbilling:Int,isbid:Int,iswin:Int,adorderid:Int,WinPrice:Double,adpayment:Double):List[Double]={
    var a = 0
    var b = 0
    var c = 0.0
    var d = 0.0
    if(iseffective==1 && isbilling ==1 ){
      if(isbid==1){
        a += 1
      }else if ( iswin ==1){
        if(adorderid!=0){
          b += 1
        }
        else{
          c += WinPrice/1000.0
          d += adpayment/1000.0
        }
      }
    }
    List(a,b,c,d)
  }


}
