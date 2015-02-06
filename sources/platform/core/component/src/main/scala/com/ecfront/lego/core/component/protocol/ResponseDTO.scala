package com.ecfront.lego.core.component.protocol

import com.ecfront.lego.core.foundation.StandardCode

case class ResponseDTO[M](code: String, message: String, body: M = null)

object Response {

  def success[M](body: M) = ResponseDTO[M](StandardCode.SUCCESS_CODE, "", body)

  def fail[M](code: String, message: String) = ResponseDTO[M](code, message)

  implicit def isSuccess[M](dto: ResponseDTO[M]): Boolean = StandardCode.SUCCESS_CODE == dto.code

}


