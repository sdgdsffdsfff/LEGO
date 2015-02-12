package com.ecfront.lego.core.component.protocol

import com.ecfront.lego.core.foundation.StandardCode

case class ResponseDTO[M](code: String, message: String, private val _body: Option[M]){
  var body:M=_
}

object Response {

  def success[M](body: M) = {
    val res=ResponseDTO[M](StandardCode.SUCCESS_CODE, "", Some(body))
    res.body=body
    res
  }

  def fail[M](code: String, message: String) ={
    ResponseDTO[M](code, message,null)
  }

  implicit def isSuccess[M](dto: ResponseDTO[M]): Boolean = StandardCode.SUCCESS_CODE == dto.code

}


