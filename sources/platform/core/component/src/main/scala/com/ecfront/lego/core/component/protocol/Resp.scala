package com.ecfront.lego.core.component.protocol

import com.ecfront.lego.core.foundation.StandardCode

case class Resp[M](code: String, message: String, private val _body: Option[M]){
  var body:M=_
}

object Resp {

  def success[M](body: M) = {
    val res=Resp[M](StandardCode.SUCCESS_CODE, "", Some(body))
    res.body=body
    res
  }

  def fail[M](code: String, message: String) ={
    Resp[M](code, message,null)
  }

  implicit def isSuccess[M](dto: Resp[M]): Boolean = StandardCode.SUCCESS_CODE == dto.code

}

case class RespSimple(
                             code: String,
                             message: String,
                             body: String=null)
